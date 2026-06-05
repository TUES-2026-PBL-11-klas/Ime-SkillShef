package com.skillchef.server.progression;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request/response payloads for the Progression Service (issue #19):
 * level progress, leaderboard, and XP transaction history.
 */
public final class ProgressionDtos {

    private ProgressionDtos() {
    }

    /** A user's current standing on the XP curve. */
    public record LevelProgressResponse(UUID userId,
                                        int globalXp,
                                        int level,
                                        int xpIntoLevel,
                                        int xpForCurrentLevel,
                                        int xpForNextLevel,
                                        int xpToNextLevel,
                                        int progressPercent) {
    }

    /** A single leaderboard row. */
    public record LeaderboardEntry(long rank,
                                   UUID userId,
                                   String username,
                                   String avatarUrl,
                                   int globalXp,
                                   int level) {
    }

    public record LeaderboardResponse(List<LeaderboardEntry> entries) {
    }

    /** One row of a user's XP transaction history. */
    public record XpTransactionDto(UUID id,
                                   int amount,
                                   String reason,
                                   String sourceType,
                                   UUID sourceId,
                                   OffsetDateTime createdAt) {
    }

    public record XpHistoryResponse(List<XpTransactionDto> transactions) {
    }
}
