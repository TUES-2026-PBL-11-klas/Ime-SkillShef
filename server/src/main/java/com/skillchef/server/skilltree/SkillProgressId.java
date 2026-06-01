package com.skillchef.server.skilltree;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SkillProgressId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    public SkillProgressId() {
    }

    public SkillProgressId(UUID userId, UUID nodeId) {
        this.userId = userId;
        this.nodeId = nodeId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SkillProgressId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId)
                && Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nodeId);
    }
}
