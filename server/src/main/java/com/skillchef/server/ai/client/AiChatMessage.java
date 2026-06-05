package com.skillchef.server.ai.client;

/**
 * A single turn in an AI chat exchange. System-level instructions are passed
 * separately via {@link AiChatRequest#systemInstruction()} rather than as a
 * message, since providers model that differently.
 */
public record AiChatMessage(Role role, String content) {

    public enum Role {
        /** Message authored by the end user. */
        USER,
        /** Message authored by the assistant (a prior AI reply). */
        ASSISTANT
    }

    public static AiChatMessage user(String content) {
        return new AiChatMessage(Role.USER, content);
    }

    public static AiChatMessage assistant(String content) {
        return new AiChatMessage(Role.ASSISTANT, content);
    }
}
