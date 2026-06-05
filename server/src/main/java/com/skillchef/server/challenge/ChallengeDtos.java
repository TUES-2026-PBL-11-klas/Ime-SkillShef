package com.skillchef.server.challenge;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Request/response payloads for the challenge and submission endpoints. */
public final class ChallengeDtos {

    private ChallengeDtos() {}

    /** Returned on GET /api/challenges — one entry per challenge. */
    public record ChallengeResponse(
            UUID id,
            String title,
            String type,
            int xpReward,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            boolean active,
            long submissionCount
    ) {}

    public record SubmissionResponse(
            UUID id,
            UUID challengeId,
            UUID userId,
            String mediaUrl,
            String aiFeedback,
            Integer score,
            OffsetDateTime createdAt
    ) {}

    /** Returned on GET /api/challenges/{id} and after a submission — includes entries. */
    public record ChallengeDetailResponse(
            UUID id,
            String title,
            String type,
            int xpReward,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            boolean active,
            List<SubmissionResponse> submissions
    ) {}
}
