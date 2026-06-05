package com.skillchef.server.notification;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.skillchef.server.notification.NotificationDtos.*;

/**
 * In-app notification inbox. All endpoints require a valid JWT.
 *
 * GET    /api/notifications                 paginated inbox, newest first
 * GET    /api/notifications/unread-count    badge count
 * PATCH  /api/notifications/{id}/read       mark one notification as read
 * PATCH  /api/notifications/read-all        mark all notifications as read
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Page<NotificationResponse> getNotifications(
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return notificationService.getNotifications(principal.userId(), pageable);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse getUnreadCount(@AuthenticationPrincipal AuthPrincipal principal) {
        return notificationService.getUnreadCount(principal.userId());
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        notificationService.markAsRead(principal.userId(), notificationId);
    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead(@AuthenticationPrincipal AuthPrincipal principal) {
        notificationService.markAllAsRead(principal.userId());
    }
}
