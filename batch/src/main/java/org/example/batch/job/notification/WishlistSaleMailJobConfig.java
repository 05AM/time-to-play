package org.example.batch.job.notification;

import java.time.OffsetDateTime;

import javax.sql.DataSource;

import org.example.batch.infra.mail.MailTemplateRenderer;
import org.example.batch.infra.mail.SmtpMailSender;
import org.example.batch.infra.persistence.EmailNotificationLogService;
import org.example.batch.job.notification.reader.WishlistSaleRowMapper;
import org.example.batch.job.notification.writer.MemberGroupingMailWriter;
import org.example.batch.model.WishlistSaleRow;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class WishlistSaleMailJobConfig {

    public static final String JOB_NAME = "wishlistSaleMailJob";
    public static final String STEP_NAME = "sendWishlistSaleMailStep";

    @Bean
    public Job wishlistSaleMailJob(JobRepository jobRepository, Step sendWishlistSaleMailStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(sendWishlistSaleMailStep)
            .build();
    }

    @Bean
    public Step sendWishlistSaleMailStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        WishlistSaleMailProperties props,
        JdbcCursorItemReader<WishlistSaleRow> wishlistSaleRowReader,
        MemberGroupingMailWriter memberGroupingMailWriter
    ) {
        return new StepBuilder(STEP_NAME, jobRepository)
            .<WishlistSaleRow, WishlistSaleRow>chunk(props.chunkSize(), transactionManager)
            .reader(wishlistSaleRowReader)
            .writer(memberGroupingMailWriter)
            .stream(memberGroupingMailWriter)
            .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<WishlistSaleRow> wishlistSaleRowReader(
        DataSource dataSource,
        WishlistSaleMailProperties props,
        @Value("#{jobParameters['from']}") String fromStr,
        @Value("#{jobParameters['to']}") String toStr
    ) {
        OffsetDateTime from = OffsetDateTime.parse(fromStr);
        OffsetDateTime to = OffsetDateTime.parse(toStr);

        String sql = """
            select
              m.id as member_id,
              m.email as member_email,
              m.name as member_name,
              pph.id as price_history_id,
              plg.platform as platform, 
              pg.name as game_name,
              coalesce(pg.main_image_url, '') as main_image_url,
              pph.price_original,
              pph.price_current,
              pph.discount_rate,
              pph.created_at as price_changed_at
            from product_price_history pph
            join wishlist w
              on w.product_game_id = pph.product_game_id
             and pph.discount_rate is not null
             and pph.discount_rate >= w.notify_discount_rate
            join member m
              on m.id = w.member_id
            join product_game pg
              on pg.id = pph.product_game_id
            join platform_game plg
              on plg.id = pg.platform_game_id
            where pph.created_at >= ?
              and pph.created_at <  ?
              and pph.price_status = 'PRICED'
              and (pph.discount_rate > 0)
              and m.deleted_at is null
              and pg.is_delisted = false
            order by m.id asc, pph.discount_rate desc, pg.name asc, pph.id asc
            """;

        return new JdbcCursorItemReaderBuilder<WishlistSaleRow>()
            .name("wishlistSaleRowReader")
            .dataSource(dataSource)
            .sql(sql)
            .rowMapper(new WishlistSaleRowMapper())
            .preparedStatementSetter(ps -> {
                ps.setObject(1, from);
                ps.setObject(2, to);
            })
            .fetchSize(props.fetchSize())
            .build();
    }

    @Bean
    @StepScope
    public MemberGroupingMailWriter memberGroupingMailWriter(
        WishlistSaleMailProperties props,
        EmailNotificationLogService logService,
        MailTemplateRenderer renderer,
        SmtpMailSender mailSender
    ) {
        return new MemberGroupingMailWriter(props, logService, renderer, mailSender);
    }
}
