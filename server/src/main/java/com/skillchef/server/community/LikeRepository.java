package com.skillchef.server.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    /** Check whether a user has already liked a post (uses uq_likes_user_post). */
    boolean existsByUserIdAndRecipePostId(UUID userId, UUID recipePostId);

    /** Retrieve the like row for a toggle/delete operation. */
    Optional<Like> findByUserIdAndRecipePostId(UUID userId, UUID recipePostId);

    /** Count likes on a post for the engagement summary (uses idx_likes_recipe_post_id). */
    long countByRecipePostId(UUID recipePostId);
}
