package com.skillchef.server.ai.client;

/**
 * Transport for AI chat completions.
 *
 * <p>This is the <em>integration</em> layer: it sends prompts and returns text.
 * It deliberately contains no SkillChef domain logic — building recipe prompts,
 * injecting user skill level, persisting {@code ai_conversations} / {@code ai_messages}
 * etc. belong to the AI Service that sits on top of this client.
 *
 * <p>The bean exposed to the application (qualifier {@code aiChatClient},
 * {@code @Primary}) is wrapped with client-side rate limiting and retry — see
 * {@link ResilientAiChatClient}.
 */
public interface AiChatClient {

    /**
     * Run a chat completion.
     *
     * @param request the prompt(s) and generation settings
     * @return the model's reply
     * @throws AiClientException on any failure; {@link AiClientException#isRetryable()}
     *                           indicates whether a retry could plausibly succeed
     */
    AiChatResult complete(AiChatRequest request);
}
