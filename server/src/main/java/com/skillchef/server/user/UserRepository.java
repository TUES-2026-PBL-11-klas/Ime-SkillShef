package com.skillchef.server.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    /** Leaderboard ordering: highest XP first, then username for stable tie-breaks. */
    List<User> findAllByOrderByGlobalXpDescUsernameAsc(Pageable pageable);

    /** Number of users with strictly more XP than the given value (used to compute rank). */
    long countByGlobalXpGreaterThan(int globalXp);
}
