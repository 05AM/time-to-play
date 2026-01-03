package org.example.batch.job.notification;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WishlistSaleMailStartupRunner implements CommandLineRunner {

    private final WishlistSaleMailProperties props;
    private final JobLauncher jobLauncher;
    private final Job wishlistSaleMailJob;

    @Override
    public void run(String... args) throws Exception {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        OffsetDateTime to = OffsetDateTime.now(zone);
        OffsetDateTime from = to.minusHours(props.lookbackHours());

        jobLauncher.run(
            wishlistSaleMailJob,
            new JobParametersBuilder()
                .addString("from", from.toString())
                .addString("to", to.toString())
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters()
        );
    }
}
