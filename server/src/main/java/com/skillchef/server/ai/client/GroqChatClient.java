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
 * {@link AiChatClient} backed by the Groq OpenAI-compatible REST API.
 *
 * <p>Transport only: maps our provider-agnostic request/response to Groq's
 * wire format (identical to OpenAI's /chat/completions). Retry and rate
 * limiting live in {@link ResilientAiChatClient}, which wraps this.
 */
public class GroqChatClient implements AiChatClient {

    private final AiProperties props;
    private final RestClient restClient;

    public GroqChatClient(AiProperties props, RestClient restClient) {
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
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + props.getApiKey())
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
        body.put("temperature", request.temperature() != null ? request.temperature() : props.getTemperature());
        body.put("max_tokens", request.maxOutputTokens() != null ? request.maxOutputTokens() : props.getMaxOutputTokens());

        List<Map<String, String>> messages = new ArrayList<>();
        if (StringUtils.hasText(request.systemInstruction())) {
            messages.add(Map.of("role", "system", "content", request.systemInstruction()));
        }
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

        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw AiClientException.nonRetryable(null, "AI returned no choices");
        }

        String text = choices.get(0).path("message").path("content").asText("");

        JsonNode usage = root.path("usage");
        Integer promptTokens = usage.has("prompt_tokens") ? usage.get("prompt_tokens").asInt() : null;
        Integer completionTokens = usage.has("completion_tokens") ? usage.get("completion_tokens").asInt() : null;

        return new AiChatResult(text, promptTokens, completionTokens);
    }

    private AiClientException fromHttpError(RestClientResponseException e) {
        HttpStatusCode status = e.getStatusCode();
        int code = status.value();
        boolean retryable = code == 429 || status.is5xxServerError();
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
