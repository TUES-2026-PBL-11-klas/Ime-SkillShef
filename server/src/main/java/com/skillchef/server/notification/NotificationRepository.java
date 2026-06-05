package com.skillchef.server.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /** Inbox — all notifications for a user, newest first (uses idx_notifications_user_id). */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /** Badge count — uses the partial index idx_notifications_unread. */
    long countByUserIdAndReadFalse(UUID userId);

    /** Bulk mark-all-read for a user. */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllReadForUser(@Param("userId") UUID userId);
}
