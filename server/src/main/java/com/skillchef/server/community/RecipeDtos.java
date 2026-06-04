package com.skillchef.server.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Request/response payloads for the recipe post and engagement endpoints. */
public final class RecipeDtos {

    private RecipeDtos() {}

    public record CreatePostRequest(
            @NotBlank @Size(max = 255) String title,
            String description,
            @Size(max = 512) String imageUrl
    ) {}

    public record UpdatePostRequest(
            @NotBlank @Size(max = 255) String title,
            String description,
            @Size(max = 512) String imageUrl
    ) {}

    public record PostResponse(
            UUID id,
            UUID userId,
            String title,
            String description,
            String imageUrl,
            OffsetDateTime createdAt
    ) {}

    /** Returned on GET /api/posts/{id} — includes engagement counts. */
    public record PostDetailResponse(
            UUID id,
            UUID userId,
            String title,
            String description,
            String imageUrl,
            long likeCount,
            long commentCount,
            OffsetDateTime createdAt
    ) {}

    public record CommentRequest(
            @NotBlank @Size(max = 2000) String content
    ) {}

    public record CommentResponse(
            UUID id,
            UUID userId,
            UUID recipePostId,
            String content,
            OffsetDateTime createdAt
    ) {}

    /** Returned on GET /api/posts/{postId}/engagement. */
    public record EngagementSummary(
            long likeCount,
            long commentCount
    ) {}
}
