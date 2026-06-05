package com.skillchef.server.ai.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Strongly-typed binding for {@code skillchef.ai.*} configuration.
 *
 * <p>The AI client is provider-agnostic; {@link #provider} selects the transport
 * implementation (currently {@code gemini}). Switching providers is a config
 * change plus a new {@link AiChatClient} implementation — no caller changes.
 */
@ConfigurationProperties(prefix = "skillchef.ai")
public class AiProperties {

    /** Transport provider to use. Currently supported: {@code gemini}. */
    private String provider = "gemini";

    /** API key for the selected provider. Required for any real call. */
    private String apiKey;

    /** Model identifier (e.g. {@code gemini-2.5-flash}). */
    private String model = "gemini-2.5-flash";

    /** Provider REST base URL (no trailing slash). */
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

    /** Client-side cap to stay under the provider's rate limit (Gemini free tier ≈ 15 RPM). */
    private int requestsPerMinute = 15;

    /** Number of retries after the initial attempt for retryable failures (429 / 5xx / timeout). */
    private int maxRetries = 3;

    /** Base delay for exponential backoff between retries. */
    private Duration retryBackoff = Duration.ofMillis(500);

    /** Upper bound for a single backoff wait. */
    private Duration maxRetryBackoff = Duration.ofSeconds(8);

    /** TCP connect timeout. */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /** Response read timeout. */
    private Duration readTimeout = Duration.ofSeconds(30);

    /** Default sampling temperature when a request does not specify one. */
    private double temperature = 0.7;

    /** Default output-token cap when a request does not specify one. */
    private int maxOutputTokens = 1024;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(Duration retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public Duration getMaxRetryBackoff() {
        return maxRetryBackoff;
    }

    public void setMaxRetryBackoff(Duration maxRetryBackoff) {
        this.maxRetryBackoff = maxRetryBackoff;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }
}
