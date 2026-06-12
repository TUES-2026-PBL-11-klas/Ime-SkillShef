package com.skillchef.server.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
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
 * {@link AiChatClient} backed by the Anthropic Messages REST API.
 *
 * <p>Transport only: maps our provider-agnostic request/response to Anthropic's
 * wire format and normalizes failures into {@link AiClientException}. Retry and
 * rate limiting live in {@link ResilientAiChatClient}, which wraps this.
 */
public class AnthropicChatClient implements AiChatClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final AiProperties props;
    private final RestClient restClient;

    public AnthropicChatClient(AiProperties props, RestClient restClient) {
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
                    .uri("/messages")
                    .header("x-api-key", props.getApiKey())
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildRequestBody(request))
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientResponseException e) {
            throw fromHttpError(e);
        } catch (ResourceAccessException e) {
            throw AiClientException.retryable(null, "AI request I/O error: " + e.getMessage(), null, e);
        }

        return parseResponse(root);
    }

    private Map<String, Object> buildRequestBody(AiChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.getModel());
        body.put("max_tokens", request.maxOutputTokens() != null ? request.maxOutputTokens() : props.getMaxOutputTokens());
        body.put("temperature", request.temperature() != null ? request.temperature() : props.getTemperature());

        if (StringUtils.hasText(request.systemInstruction())) {
            body.put("system", request.systemInstruction());
        }

        List<Map<String, Object>> messages = new ArrayList<>();
        for (AiChatMessage message : request.messages()) {
            messages.add(Map.of(
                    "role", message.role() == AiChatMessage.Role.ASSISTANT ? "assistant" : "user",
                    "content", message.content()));
        }
        body.put("messages", messages);

        return body;
    }

    private AiChatResult parseResponse(JsonNode root) {
        if (root == null) {
            throw AiClientException.nonRetryable(null, "AI response was empty");
        }

        JsonNode content = root.path("content");
        if (!content.isArray() || content.isEmpty()) {
            String stopReason = root.path("stop_reason").asText("");
            throw AiClientException.nonRetryable(null, "AI returned no content (stop_reason=" + stopReason + ")");
        }

        StringBuilder text = new StringBuilder();
        for (JsonNode block : content) {
            if ("text".equals(block.path("type").asText())) {
                text.append(block.path("text").asText(""));
            }
        }

        JsonNode usage = root.path("usage");
        Integer inputTokens = usage.has("input_tokens") ? usage.get("input_tokens").asInt() : null;
        Integer outputTokens = usage.has("output_tokens") ? usage.get("output_tokens").asInt() : null;

        return new AiChatResult(text.toString(), inputTokens, outputTokens);
    }

    private AiClientException fromHttpError(RestClientResponseException e) {
        HttpStatusCode status = e.getStatusCode();
        int code = status.value();
        boolean retryable = code == 429 || code == 529 || status.is5xxServerError();
        String message = "AI provider returned HTTP " + code + ": " + truncate(e.getResponseBodyAsString());
        if (retryable) {
            Duration retryAfter = null;
            if (e.getResponseHeaders() != null) {
                String val = e.getResponseHeaders().getFirst("retry-after");
                if (val != null) {
                    try { retryAfter = Duration.ofSeconds(Long.parseLong(val.trim())); } catch (NumberFormatException ignored) {}
                }
            }
            return AiClientException.retryable(code, message, retryAfter, e);
        }
        return new AiClientException(message, false, code, null, e);
    }

    private static String truncate(String body) {
        if (body == null) return "";
        return body.length() <= 500 ? body : body.substring(0, 500) + "…";
    }
}
