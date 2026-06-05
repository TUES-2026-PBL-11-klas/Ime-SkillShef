package com.skillchef.server.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Request/response payloads for the notification endpoints. */
public final class NotificationDtos {

    private NotificationDtos() {}

    public record NotificationResponse(
            UUID id,
            UUID actorId,
            NotificationType type,
            UUID entityId,
            boolean read,
            OffsetDateTime createdAt
    ) {}

    public record UnreadCountResponse(long count) {}
}
