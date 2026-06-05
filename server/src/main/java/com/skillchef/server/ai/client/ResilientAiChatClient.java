package com.skillchef.server.ai.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Wraps a transport {@link AiChatClient} with two cross-cutting concerns:
 *
 * <ul>
 *   <li><b>Rate limiting</b> — a client-side {@link WindowRateLimiter} keeps us
 *       under the provider's requests-per-minute quota.</li>
 *   <li><b>Retry</b> — retryable failures (429 / 5xx / timeout) are retried up to
 *       {@code maxRetries} times with exponential backoff, honoring a
 *       {@code Retry-After} hint from the provider when present.</li>
 * </ul>
 *
 * Non-retryable failures (bad request, missing key, safety block) propagate
 * immediately.
 */
public class ResilientAiChatClient implements AiChatClient {

    private static final Logger log = LoggerFactory.getLogger(ResilientAiChatClient.class);

    private final AiChatClient delegate;
    private final AiProperties props;
    private final WindowRateLimiter rateLimiter;

    public ResilientAiChatClient(AiChatClient delegate, AiProperties props) {
        this(delegate, props, new WindowRateLimiter(props.getRequestsPerMinute(), Duration.ofMinutes(1)));
    }

    ResilientAiChatClient(AiChatClient delegate, AiProperties props, WindowRateLimiter rateLimiter) {
        this.delegate = delegate;
        this.props = props;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public AiChatResult complete(AiChatRequest request) {
        rateLimiter.acquire();

        int maxAttempts = Math.max(1, props.getMaxRetries() + 1);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return delegate.complete(request);
            } catch (AiClientException e) {
                if (!e.isRetryable() || attempt == maxAttempts) {
                    throw e;
                }
                Duration wait = backoff(attempt, e.getRetryAfter());
                log.warn("AI call failed (attempt {}/{}, status={}): {} — retrying in {} ms",
                        attempt, maxAttempts, e.getStatusCode(), e.getMessage(), wait.toMillis());
                sleep(wait);
            }
        }
        // Unreachable: the final attempt either returns or throws above.
        throw new IllegalStateException("AI retry loop exited without a result");
    }

    /** Exponential backoff (base · 2^(attempt-1)), capped, or the server's Retry-After if larger. */
    private Duration backoff(int attempt, Duration retryAfter) {
        long base = props.getRetryBackoff().toMillis();
        long exponential = base << (attempt - 1);
        long capped = Math.min(exponential, props.getMaxRetryBackoff().toMillis());
        long millis = retryAfter != null ? Math.max(capped, retryAfter.toMillis()) : capped;
        return Duration.ofMillis(Math.max(0, millis));
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiClientException("Interrupted during AI retry backoff", false, null, null, e);
        }
    }
}
