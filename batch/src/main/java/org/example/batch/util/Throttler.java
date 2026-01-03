package org.example.batch.util;

public interface Throttler {

    void throttle();

    void throttle(int min, int max);
}
