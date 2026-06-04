package com.skillchef.server.ai.client;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class WindowRateLimiterTest {

    @Test
    void allowsUpToMaxPermitsWithinWindowThenBlocks() {
        AtomicLong now = new AtomicLong(0);
        WindowRateLimiter limiter = new WindowRateLimiter(2, Duration.ofSeconds(60), now::get, 1);

        assertThat(limiter.tryAcquire()).isTrue();
        assertThat(limiter.tryAcquire()).isTrue();
        assertThat(limiter.tryAcquire()).as("third permit within window is denied").isFalse();
    }

    @Test
    void replenishesPermitsOnceTheWindowSlidesPast() {
        AtomicLong now = new AtomicLong(0);
        WindowRateLimiter limiter = new WindowRateLimiter(1, Duration.ofSeconds(60), now::get, 1);

        assertThat(limiter.tryAcquire()).isTrue();
        assertThat(limiter.tryAcquire()).isFalse();

        now.addAndGet(Duration.ofSeconds(61).toNanos());

        assertThat(limiter.tryAcquire()).as("permit freed after window elapses").isTrue();
    }
}
