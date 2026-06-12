package com.skillchef.server.challenge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    /** All challenges, most recently started first. */
    List<Challenge> findAllByOrderByStartDateDesc();
}
