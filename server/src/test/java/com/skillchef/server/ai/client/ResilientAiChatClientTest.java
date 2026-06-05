package com.skillchef.server.ai.client;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResilientAiChatClientTest {

    /** Fast, deterministic props: tiny backoff, no real rate limiting. */
    private static AiProperties fastProps(int maxRetries) {
        AiProperties props = new AiProperties();
        props.setMaxRetries(maxRetries);
        props.setRetryBackoff(Duration.ofMillis(1));
        props.setMaxRetryBackoff(Duration.ofMillis(2));
        props.setRequestsPerMinute(1000);
        return props;
    }

    @Test
    void retriesRetryableFailuresThenSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        AiChatClient delegate = request -> {
            if (calls.incrementAndGet() < 3) {
                throw AiClientException.retryable(503, "transient", null);
            }
            return new AiChatResult("ok", null, null);
        };

        ResilientAiChatClient client = new ResilientAiChatClient(delegate, fastProps(3));

        assertThat(client.complete(AiChatRequest.userPrompt("hi")).content()).isEqualTo("ok");
        assertThat(calls.get()).isEqualTo(3);
    }

    @Test
    void doesNotRetryNonRetryableFailures() {
        AtomicInteger calls = new AtomicInteger();
        AiChatClient delegate = request -> {
            calls.incrementAndGet();
            throw AiClientException.nonRetryable(400, "bad request");
        };

        ResilientAiChatClient client = new ResilientAiChatClient(delegate, fastProps(3));

        assertThatThrownBy(() -> client.complete(AiChatRequest.userPrompt("hi")))
                .isInstanceOf(AiClientException.class);
        assertThat(calls.get()).as("non-retryable failure attempted exactly once").isEqualTo(1);
    }

    @Test
    void givesUpAfterMaxRetriesAndPropagatesLastError() {
        AtomicInteger calls = new AtomicInteger();
        AiChatClient delegate = request -> {
            calls.incrementAndGet();
            throw AiClientException.retryable(429, "always rate limited", null);
        };

        ResilientAiChatClient client = new ResilientAiChatClient(delegate, fastProps(2));

        assertThatThrownBy(() -> client.complete(AiChatRequest.userPrompt("hi")))
                .isInstanceOf(AiClientException.class)
                .satisfies(e -> assertThat(((AiClientException) e).getStatusCode()).isEqualTo(429));
        assertThat(calls.get()).as("initial attempt + 2 retries").isEqualTo(3);
    }
}
