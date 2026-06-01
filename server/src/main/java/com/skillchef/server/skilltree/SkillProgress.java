package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Per-user progress on a single {@link SkillNode}. A row is created when a node
 * is unlocked and updated as the user advances; {@code completedAt} marks the
 * node as finished for the purpose of unlocking higher tiers.
 */
@Entity
@Table(name = "skill_progress")
public class SkillProgress {

    @EmbeddedId
    private SkillProgressId id;

    @Column(name = "progress_percent", nullable = false)
    private int progressPercent = 0;

    @Column(name = "unlocked_at")
    private OffsetDateTime unlockedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public SkillProgressId getId() {
        return id;
    }

    public void setId(SkillProgressId id) {
        this.id = id;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public OffsetDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(OffsetDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
