package org.example.batch.util;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class ThrottlerImpl implements Throttler {

    private static final int MIN_SEC = 1;
    private static final int MAX_SEC = 3;

    @Override
    public void throttle() {
        throttle(MIN_SEC, MAX_SEC);
    }

    @Override
    public void throttle(int minSec, int maxSec) {
        if (minSec <= 0 || maxSec < minSec) {
            throw new IllegalArgumentException("invalid sleep seconds");
        }

        int sec = ThreadLocalRandom.current().nextInt(minSec, maxSec + 1);
        try {
            Thread.sleep(Duration.ofSeconds(sec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("sleep interrupted", e);
        }
    }
}
