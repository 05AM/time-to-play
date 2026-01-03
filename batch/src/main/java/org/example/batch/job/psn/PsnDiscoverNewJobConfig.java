package org.example.batch.job.psn;

import org.example.batch.tasklet.psn.GameDetailFetchTasklet;
import org.example.batch.tasklet.psn.DiscoverNewGameTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PsnDiscoverNewJobConfig {

    public static final String JOB_NAME = "psnDiscoverNewJob";

    @Bean
    public Job psnDiscoverNewJob(
        JobRepository jobRepository,
        Step psnDiscoverNewStep,
        Step psnDetailFetchStep
    ) {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(psnDiscoverNewStep)
            .next(psnDetailFetchStep)
            .build();
    }

    @Bean
    public Step psnDiscoverNewStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DiscoverNewGameTasklet tasklet
    ) {
        return new StepBuilder("psnDiscoverNewStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public Step psnDetailFetchStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        GameDetailFetchTasklet tasklet
    ) {
        return new StepBuilder("psnDetailFetchStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
}
