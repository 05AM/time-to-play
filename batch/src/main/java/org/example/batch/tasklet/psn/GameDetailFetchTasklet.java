package org.example.batch.tasklet.psn;

import java.util.Optional;

import org.example.batch.application.PsnGameDetailPersistService;
import org.example.batch.infra.client.psn.PsnGraphQlClient;
import org.example.batch.infra.client.psn.dto.request.ConceptDetailVariables;
import org.example.batch.infra.client.psn.dto.response.GraphQlRes;
import org.example.batch.infra.client.psn.dto.response.PsnConceptGameDetailRes;
import org.example.batch.infra.mapper.PsnGraphQlResponseMapper;
import org.example.batch.infra.persistence.PlatformGameDao;
import org.example.batch.util.Throttler;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDetailFetchTasklet implements Tasklet {

    private static final String OP_KEY = "conceptRetrieveForUpsellWithCtas";

    private final PsnGraphQlClient client;
    private final PsnGraphQlResponseMapper mapper;
    private final PlatformGameDao platformGameDao;
    private final PsnGameDetailPersistService gameDetailPersistService;
    private final Throttler throttler;
    private final ObjectMapper objectMapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Optional<PlatformGameDao.PlatformGameRow> opt = platformGameDao.findNextCreatedForUpdate();
        if (opt.isEmpty()) return RepeatStatus.FINISHED;

        PlatformGameDao.PlatformGameRow platformGame = opt.get();

        JsonNode result = client.execute(OP_KEY, new ConceptDetailVariables(platformGame.platformRootId()));
        throttler.throttle();

        GraphQlRes<PsnConceptGameDetailRes> res = mapper.toResponse(result, PsnConceptGameDetailRes.class);

        if (res == null) throw new IllegalStateException("PSN response is null");
        if (res.hasErrors()) throw new IllegalStateException("PSN failed: " + res.errors());
        if (res.data() == null || res.data().conceptRetrieve() == null) {
            log.warn("[PSN][DETAIL] empty data");
            return RepeatStatus.FINISHED;
        }

        try {
            log.info("[PSN][RAW RESPONSE]\n{}",
                objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(res)
            );
        } catch (Exception e) {
            log.warn("[PSN][RAW RESPONSE] logging failed", e);
        }

        gameDetailPersistService.persist(platformGame, res.data().conceptRetrieve());
        return RepeatStatus.CONTINUABLE;
    }
}
