package org.example.batch.tasklet.psn;

import java.util.List;

import org.example.batch.application.PsnDiscoverNewService;
import org.example.batch.infra.client.psn.PsnCategoryIds;
import org.example.batch.infra.client.psn.PsnGraphQlClient;
import org.example.batch.infra.client.psn.dto.request.ConceptListVariables;
import org.example.batch.infra.client.psn.dto.response.GraphQlRes;
import org.example.batch.infra.client.psn.dto.response.PsnConceptGameListRes;
import org.example.batch.infra.mapper.PsnGraphQlResponseMapper;
import org.example.batch.util.Throttler;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscoverNewGameTasklet implements Tasklet {

    private static final String OP_KEY = "categoryGridRetrieve";
    private static final int SIZE = 100;

    private final PsnGraphQlClient client;
    private final PsnGraphQlResponseMapper mapper;
    private final PsnDiscoverNewService service;
    private final Throttler throttler;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var stepContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        int offset = stepContext.containsKey("psn.offset")
            ? stepContext.getInt("psn.offset") : 0;

        // 신작순 정렬
        ConceptListVariables requestVariables = new ConceptListVariables(
            PsnCategoryIds.ALL_PRODUCT_GRID,
            new ConceptListVariables.PageArgs(SIZE, offset),
            new ConceptListVariables.SortBy("conceptReleaseDate", false),
            // null,    // 베스트셀러순 정렬
            List.of(),
            List.of(),
            200
        );

        // 파싱
        JsonNode result = client.execute(OP_KEY, requestVariables);
        GraphQlRes<PsnConceptGameListRes> response = mapper.toResponse(result, PsnConceptGameListRes.class);

        if (response == null) {
            throw new IllegalStateException("PSN response is null");
        }
        if (response.hasErrors()) {
            throw new IllegalStateException("PSN failed: " + response.errors());
        }
        if (response.data() == null || response.data().categoryGridRetrieve() == null) {
            log.warn("[PSN][DISCOVER] empty data");
            return RepeatStatus.FINISHED;
        }

        // 조회한 핵심 데이터
        List<PsnConceptGameListRes.Concept> concepts = response.data().categoryGridRetrieve().concepts();

        if (concepts == null || concepts.isEmpty()) {
            log.info("[PSN][DISCOVER] concepts=0");
            return RepeatStatus.FINISHED;
        }

        service.persist(concepts);
        stepContext.putInt("psn.offset", offset + SIZE);

        throttler.throttle();
        return RepeatStatus.CONTINUABLE;
    }
}