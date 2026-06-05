package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class SkillTreeDtos {

    private SkillTreeDtos() {
    }

    public record SkillTreeResponse(List<DomainDto> domains) {
    }

    public record DomainDto(UUID id, String name, String description, List<NodeDto> nodes) {
    }

    public record NodeDto(UUID id,
                          UUID domainId,
                          UUID parentNodeId,
                          String title,
                          String description,
                          int tier,
                          int xpReward,
                          boolean unlockable,
                          ProgressDto progress,
                          List<LessonDto> lessons,
                          List<QuizDto> quizzes) {
    }

    public record ProgressDto(int progressPercent,
                              OffsetDateTime unlockedAt,
                              OffsetDateTime completedAt) {
    }

    public record LessonDto(UUID id,
                            UUID nodeId,
                            String title,
                            String description,
                            String videoUrl,
                            int durationSeconds) {
    }

    public record QuizDto(UUID id,
                          UUID nodeId,
                          String question,
                          List<String> options) {
    }
}
