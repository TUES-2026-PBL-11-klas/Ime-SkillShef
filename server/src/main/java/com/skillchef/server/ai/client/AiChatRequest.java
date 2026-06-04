package com.skillchef.server.ai.client;

import java.util.List;

/**
 * A provider-agnostic chat completion request.
 *
 * <p>{@code systemInstruction}, {@code temperature} and {@code maxOutputTokens}
 * are optional ({@code null} ⇒ fall back to the configured defaults in
 * {@link AiProperties}).
 */
public record AiChatRequest(
        String systemInstruction,
        List<AiChatMessage> messages,
        Double temperature,
        Integer maxOutputTokens) {

    /** Single user prompt with no system instruction and default generation settings. */
    public static AiChatRequest userPrompt(String prompt) {
        return new AiChatRequest(null, List.of(AiChatMessage.user(prompt)), null, null);
    }

    /** System instruction + a single user prompt with default generation settings. */
    public static AiChatRequest of(String systemInstruction, String prompt) {
        return new AiChatRequest(systemInstruction, List.of(AiChatMessage.user(prompt)), null, null);
    }

    /** System instruction + a full message history with default generation settings. */
    public static AiChatRequest of(String systemInstruction, List<AiChatMessage> messages) {
        return new AiChatRequest(systemInstruction, messages, null, null);
    }
}
