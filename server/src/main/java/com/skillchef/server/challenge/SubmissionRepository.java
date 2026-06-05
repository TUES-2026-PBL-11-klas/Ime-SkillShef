package com.skillchef.server.challenge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    /** A challenge's submissions, newest first (uses idx_submissions_challenge_id). */
    List<Submission> findByChallengeIdOrderByCreatedAtDesc(UUID challengeId);

    /** Submission count for a challenge, shown on the list. */
    long countByChallengeId(UUID challengeId);
}
