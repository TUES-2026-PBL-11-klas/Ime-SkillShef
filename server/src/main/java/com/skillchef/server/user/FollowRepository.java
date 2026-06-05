package com.skillchef.server.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    /** Number of users following {@code userId} (their follower count). */
    long countByIdFollowingId(UUID userId);

    /** Number of users {@code userId} follows (their following count). */
    long countByIdFollowerId(UUID userId);

    /**
     * Users who follow {@code userId}, most recent first.
     * Ad-hoc entity join on the {@code follower_id} side of the relationship.
     */
    @Query("select u from Follow f join User u on u.id = f.id.followerId "
            + "where f.id.followingId = :userId order by f.createdAt desc")
    Page<User> findFollowers(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Users that {@code userId} follows, most recent first.
     * Ad-hoc entity join on the {@code following_id} side of the relationship.
     */
    @Query("select u from Follow f join User u on u.id = f.id.followingId "
            + "where f.id.followerId = :userId order by f.createdAt desc")
    Page<User> findFollowing(@Param("userId") UUID userId, Pageable pageable);

    /**
     * The follows feed query consumed by the community feed (Person 4): the ids of every
     * user that {@code userId} follows, used to pull recipes from people they follow.
     */
    @Query("select f.id.followingId from Follow f where f.id.followerId = :userId")
    List<UUID> findFollowingIds(@Param("userId") UUID userId);
}
