package com.skillchef.server.ai.client;

/**
 * The result of a chat completion. Token counts are best-effort and may be
 * {@code null} if the provider does not report usage.
 */
public record AiChatResult(String content, Integer promptTokens, Integer completionTokens) {
}
