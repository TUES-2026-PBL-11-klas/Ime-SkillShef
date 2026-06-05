package com.skillchef.server.notification;

import com.skillchef.server.community.RecipePost;
import com.skillchef.server.community.RecipePostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.skillchef.server.notification.NotificationDtos.*;

/**
 * Creates in-app notifications and triggers email dispatch.
 *
 * Integration points:
 *   Called by {@link com.skillchef.server.community.EngagementService} after
 *   likePost and addComment.
 *
 *   For FOLLOW notifications: Person 1's follow service should call
 *   {@link #onUserFollowed(UUID, UUID)} after persisting the follow row.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final RecipePostRepository postRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               RecipePostRepository postRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.postRepository = postRepository;
        this.emailService = emailService;
    }

    // --- event handlers (called by other services) ---

    /** Called after a user likes a recipe post. */
    @Transactional
    public void onPostLiked(UUID actorId, UUID postId) {
        RecipePost post = postRepository.findById(postId).orElse(null);
        if (post == null || post.getUserId().equals(actorId)) return;
        create(post.getUserId(), actorId, NotificationType.LIKE, postId);
        emailService.sendNotificationEmailAsync(post.getUserId(), NotificationType.LIKE);
    }

    /** Called after a user comments on a recipe post. */
    @Transactional
    public void onPostCommented(UUID actorId, UUID postId) {
        RecipePost post = postRepository.findById(postId).orElse(null);
        if (post == null || post.getUserId().equals(actorId)) return;
        create(post.getUserId(), actorId, NotificationType.COMMENT, postId);
        emailService.sendNotificationEmailAsync(post.getUserId(), NotificationType.COMMENT);
    }

    /**
     * Called after a user follows another user.
     * Person 1's follow service should call this method after persisting the follow.
     */
    @Transactional
    public void onUserFollowed(UUID followedUserId, UUID followerId) {
        if (followedUserId.equals(followerId)) return;
        create(followedUserId, followerId, NotificationType.FOLLOW, null);
        emailService.sendNotificationEmailAsync(followedUserId, NotificationType.FOLLOW);
    }

    // --- query / mutation (called by NotificationController) ---

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationService::toResponse);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(UUID userId) {
        return new UnreadCountResponse(notificationRepository.countByUserIdAndReadFalse(userId));
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(notificationId));
        if (!n.getUserId().equals(userId)) {
            throw new NotificationException(notificationId);
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllReadForUser(userId);
    }

    // --- helpers ---

    private void create(UUID userId, UUID actorId, NotificationType type, UUID entityId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setActorId(actorId);
        n.setType(type);
        n.setEntityId(entityId);
        notificationRepository.save(n);
    }

    private static NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getActorId(), n.getType(),
                n.getEntityId(), n.isRead(), n.getCreatedAt());
    }

    /** Thrown when a notification is not found or doesn't belong to the requesting user. */
    static class NotificationException extends RuntimeException {
        NotificationException(UUID id) {
            super("Notification not found: " + id);
        }
    }
}
