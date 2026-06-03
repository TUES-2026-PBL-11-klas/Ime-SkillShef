package com.skillchef.server.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipePostRepository extends JpaRepository<RecipePost, UUID> {

    /** All posts by a specific user, newest first (uses idx_recipe_posts_user_id). */
    Page<RecipePost> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
