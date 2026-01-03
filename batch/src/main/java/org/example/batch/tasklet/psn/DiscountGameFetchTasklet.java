package org.example.batch.tasklet.psn;

import java.time.Instant;
import java.util.List;

import org.example.batch.application.PsnDiscountSyncService;
import org.example.batch.infra.client.psn.PsnCategoryIds;
import org.example.batch.infra.client.psn.PsnGraphQlClient;
import org.example.batch.infra.client.psn.dto.request.ConceptListVariables;
import org.example.batch.infra.client.psn.dto.response.GraphQlRes;
import org.example.batch.infra.client.psn.dto.response.PsnDiscountProductListRes;
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
public class DiscountGameFetchTasklet implements Tasklet {

    private static final String OP_KEY = "categoryGridRetrieve";
    private static final int SIZE = 200;

    private final PsnGraphQlClient client;
    private final PsnGraphQlResponseMapper mapper;
    private final PsnDiscountSyncService service;
    private final Throttler throttler;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var ctx = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        int offset = ctx.containsKey("psn.discount.offset") ? ctx.getInt("psn.discount.offset") : 0;

        Instant snapshotAt;
        if (ctx.containsKey("psn.discount.snapshotAt")) {
            snapshotAt = (Instant) ctx.get("psn.discount.snapshotAt");
        } else {
            snapshotAt = Instant.now();
            ctx.put("psn.discount.snapshotAt", snapshotAt);
        }

        ConceptListVariables variables = new ConceptListVariables(
            PsnCategoryIds.DISCOUNT_PRODUCT_GRID,
            new ConceptListVariables.PageArgs(SIZE, offset),
            null,
            List.of(),
            List.of(),
            200
        );

        JsonNode result = client.execute(OP_KEY, variables);
        GraphQlRes<PsnDiscountProductListRes> response = mapper.toResponse(result, PsnDiscountProductListRes.class);

        if (response == null) throw new IllegalStateException("PSN response is null");
        if (response.hasErrors()) throw new IllegalStateException("PSN failed: " + response.errors());
        if (response.data() == null || response.data().categoryGridRetrieve() == null) {
            log.warn("[PSN][DISCOUNT] empty data");
            return RepeatStatus.FINISHED;
        }

        var grid = response.data().categoryGridRetrieve();
        List<PsnDiscountProductListRes.Product> products = grid.products();

        if (products == null || products.isEmpty()) {
            log.info("[PSN][DISCOUNT] products=0");
            return RepeatStatus.FINISHED;
        }

        service.syncDiscounts(products, snapshotAt);

        if (grid.pageInfo() != null && grid.pageInfo().isLast()) {
            log.info("[PSN][DISCOUNT] reached last page. offset={}, total={}", offset, grid.pageInfo().totalCount());
            return RepeatStatus.FINISHED;
        }

        ctx.putInt("psn.discount.offset", offset + SIZE);
        throttler.throttle();
        return RepeatStatus.CONTINUABLE;
    }
}
