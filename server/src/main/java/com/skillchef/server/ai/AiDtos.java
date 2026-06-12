package com.skillchef.server.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Request/response payloads for the AI Chef Assistant endpoints. */
public final class AiDtos {

    private AiDtos() {}

    public record SendMessageRequest(
            @NotBlank @Size(max = 4000) String content
    ) {}

    public record MessageResponse(
            UUID id,
            String role,
            String content,
            OffsetDateTime createdAt
    ) {}

    /** Returned on GET /api/ai/conversations — one entry per conversation, no messages. */
    public record ConversationSummary(
            UUID id,
            String title,
            OffsetDateTime createdAt
    ) {}

    /** Returned on conversation create / get / send — includes the full message history. */
    public record ConversationDetail(
            UUID id,
            String title,
            OffsetDateTime createdAt,
            List<MessageResponse> messages
    ) {}
}
