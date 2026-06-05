package com.skillchef.server.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AiChatClient} backed by Google's Gemini {@code generateContent} REST API.
 *
 * <p>Transport only: it maps our provider-agnostic request/response to Gemini's
 * wire format and normalizes failures into {@link AiClientException}. Retry and
 * rate limiting live in {@link ResilientAiChatClient}, which wraps this.
 */
public class GeminiChatClient implements AiChatClient {

    private static final String API_KEY_HEADER = "x-goog-api-key";

    private final AiProperties props;
    private final RestClient restClient;

    public GeminiChatClient(AiProperties props, RestClient restClient) {
        this.props = props;
        this.restClient = restClient;
    }

    @Override
    public AiChatResult complete(AiChatRequest request) {
        if (!StringUtils.hasText(props.getApiKey())) {
            throw AiClientException.nonRetryable(null,
                    "AI client is not configured: set skillchef.ai.api-key (AI_API_KEY)");
        }
        if (request.messages() == null || request.messages().isEmpty()) {
            throw AiClientException.nonRetryable(null, "AI request must contain at least one message");
        }

        JsonNode root;
        try {
            root = restClient.post()
                    .uri("/models/{model}:generateContent", props.getModel())
                    .header(API_KEY_HEADER, props.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildRequestBody(request))
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException e) {
            throw fromHttpError(e);
        } catch (ResourceAccessException e) {
            // Connect/read timeouts and other I/O — transient, worth retrying.
            throw AiClientException.retryable(null, "AI request I/O error: " + e.getMessage(), null, e);
        }

        return parseResponse(root);
    }

    private Map<String, Object> buildRequestBody(AiChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        if (StringUtils.hasText(request.systemInstruction())) {
            body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", request.systemInstruction()))));
        }

        List<Map<String, Object>> contents = new ArrayList<>();
        for (AiChatMessage message : request.messages()) {
            contents.add(Map.of(
                    "role", geminiRole(message.role()),
                    "parts", List.of(Map.of("text", message.content()))));
        }
        body.put("contents", contents);

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature",
                request.temperature() != null ? request.temperature() : props.getTemperature());
        generationConfig.put("maxOutputTokens",
                request.maxOutputTokens() != null ? request.maxOutputTokens() : props.getMaxOutputTokens());
        body.put("generationConfig", generationConfig);

        return body;
    }

    private static String geminiRole(AiChatMessage.Role role) {
        return role == AiChatMessage.Role.ASSISTANT ? "model" : "user";
    }

    private AiChatResult parseResponse(JsonNode root) {
        if (root == null) {
            throw AiClientException.nonRetryable(null, "AI response was empty");
        }

        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            // No candidate usually means a safety block or an empty generation.
            String reason = root.path("promptFeedback").path("blockReason").asText("");
            String detail = StringUtils.hasText(reason) ? " (blockReason=" + reason + ")" : "";
            throw AiClientException.nonRetryable(null, "AI returned no candidates" + detail);
        }

        StringBuilder text = new StringBuilder();
        for (JsonNode part : candidates.get(0).path("content").path("parts")) {
            text.append(part.path("text").asText(""));
        }

        JsonNode usage = root.path("usageMetadata");
        Integer promptTokens = usage.has("promptTokenCount") ? usage.get("promptTokenCount").asInt() : null;
        Integer completionTokens = usage.has("candidatesTokenCount") ? usage.get("candidatesTokenCount").asInt() : null;

        return new AiChatResult(text.toString(), promptTokens, completionTokens);
    }

    private AiClientException fromHttpError(RestClientResponseException e) {
        HttpStatusCode status = e.getStatusCode();
        int code = status.value();
        // 429 (rate limited) and any 5xx are transient; other 4xx are caller errors.
        boolean retryable = code == 429 || status.is5xxServerError();
        String message = "AI provider returned HTTP " + code + ": " + truncate(e.getResponseBodyAsString());
        if (retryable) {
            return AiClientException.retryable(code, message, parseRetryAfter(e.getResponseHeaders()), e);
        }
        return new AiClientException(message, false, code, null, e);
    }

    private static Duration parseRetryAfter(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        String value = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (value == null) {
            return null;
        }
        try {
            // Delta-seconds form (the HTTP-date form is rare for these APIs and safely ignored).
            return Duration.ofSeconds(Long.parseLong(value.trim()));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 500 ? body : body.substring(0, 500) + "…";
    }
}
