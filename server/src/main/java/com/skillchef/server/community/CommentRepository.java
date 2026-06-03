package com.skillchef.server.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /** Paginate a post's comments chronologically (uses idx_comments_recipe_post_id). */
    Page<Comment> findByRecipePostIdOrderByCreatedAtAsc(UUID recipePostId, Pageable pageable);

    /** Count comments on a post for the engagement summary. */
    long countByRecipePostId(UUID recipePostId);
}
