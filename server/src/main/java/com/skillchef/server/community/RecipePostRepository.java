package com.skillchef.server.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RecipePostRepository extends JpaRepository<RecipePost, UUID> {

    /** All posts by a specific user, newest first (uses idx_recipe_posts_user_id). */
    Page<RecipePost> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Community following feed — posts from accounts the user follows, newest first.
     * Joins on the FOLLOWS table owned by Person 1 via a JPQL subquery.
     */
    @Query("SELECT p FROM RecipePost p " +
           "WHERE p.userId IN " +
           "  (SELECT f.id.followingId FROM com.skillchef.server.user.Follow f " +
           "   WHERE f.id.followerId = :userId) " +
           "ORDER BY p.createdAt DESC")
    Page<RecipePost> findFollowingFeed(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Trending feed — all posts sorted by like count in the last 24 hours descending,
     * with created_at as a tiebreaker. Uses a conditional LEFT JOIN to count only
     * recent likes without excluding posts that have none.
     *
     * <p>Sort order is fixed in the query; only page/size from {@code pageable} are used.
     */
    @Query(value =
           "SELECT p.* FROM recipe_posts p " +
           "LEFT JOIN likes l " +
           "    ON l.recipe_post_id = p.id AND l.created_at > now() - INTERVAL '24 hours' " +
           "GROUP BY p.id " +
           "ORDER BY COUNT(l.id) DESC, p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM recipe_posts",
           nativeQuery = true)
    Page<RecipePost> findTrendingFeed(Pageable pageable);
}
