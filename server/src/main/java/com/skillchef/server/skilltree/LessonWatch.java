package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Marks that a user has watched a {@link Lesson}. Existence of the row is the
 * "watched" signal; {@code watchedAt} is refreshed on subsequent views.
 */
@Entity
@Table(name = "lesson_watches")
public class LessonWatch {

    @EmbeddedId
    private LessonWatchId id;

    @Column(name = "watched_at", nullable = false)
    private OffsetDateTime watchedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    public LessonWatchId getId() {
        return id;
    }

    public void setId(LessonWatchId id) {
        this.id = id;
    }

    public OffsetDateTime getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(OffsetDateTime watchedAt) {
        this.watchedAt = watchedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
