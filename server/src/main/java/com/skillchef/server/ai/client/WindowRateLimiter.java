package com.skillchef.server.ai.client;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.LongSupplier;

/**
 * A simple sliding-window rate limiter: at most {@code maxPermits} acquisitions
 * are allowed within any {@code window}. Intended to keep us under a provider's
 * request-per-minute quota.
 *
 * <p>In-process only (sufficient for a single backend instance in the MVP). The
 * clock is injectable so the window logic can be unit-tested deterministically.
 */
public class WindowRateLimiter {

    private final int maxPermits;
    private final long windowNanos;
    private final LongSupplier nanoClock;
    private final long sleepMillis;
    private final Deque<Long> hits = new ArrayDeque<>();

    /** Production constructor: uses {@link System#nanoTime()} and polls every 50 ms. */
    public WindowRateLimiter(int maxPermits, Duration window) {
        this(maxPermits, window, System::nanoTime, 50);
    }

    WindowRateLimiter(int maxPermits, Duration window, LongSupplier nanoClock, long sleepMillis) {
        this.maxPermits = Math.max(1, maxPermits);
        this.windowNanos = window.toNanos();
        this.nanoClock = nanoClock;
        this.sleepMillis = sleepMillis;
    }

    /** Block until a permit is available. */
    public void acquire() {
        while (!tryAcquire()) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AiClientException("Interrupted while waiting for AI rate-limit permit",
                        false, null, null, e);
            }
        }
    }

    /** Non-blocking: take a permit if one is free in the current window. */
    synchronized boolean tryAcquire() {
        long now = nanoClock.getAsLong();
        long cutoff = now - windowNanos;
        while (!hits.isEmpty() && hits.peekFirst() <= cutoff) {
            hits.pollFirst();
        }
        if (hits.size() >= maxPermits) {
            return false;
        }
        hits.addLast(now);
        return true;
    }
}
