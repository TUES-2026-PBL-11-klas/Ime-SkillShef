package com.skillchef.server.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.skillchef.server.community.RecipeDtos.*;

/**
 * Application logic for recipe post CRUD.
 *
 * Image upload is handled upstream via the storage layer
 * (POST /api/storage/presign → PUT to S3/R2 → POST /api/storage/process).
 * Callers pass the resulting CDN URL as {@code imageUrl} in the request body.
 */
@Service
public class RecipeService {

    private final RecipePostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public RecipeService(RecipePostRepository postRepository,
                         LikeRepository likeRepository,
                         CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public PostResponse createPost(UUID userId, CreatePostRequest req) {
        RecipePost post = new RecipePost();
        post.setUserId(userId);
        post.setTitle(req.title());
        post.setDescription(req.description());
        post.setImageUrl(req.imageUrl());
        post = postRepository.save(post);
        return toResponse(post);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(UUID postId) {
        RecipePost post = requirePost(postId);
        long likes    = likeRepository.countByRecipePostId(postId);
        long comments = commentRepository.countByRecipePostId(postId);
        return toDetailResponse(post, likes, comments);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(UUID userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(RecipeService::toResponse);
    }

    @Transactional
    public PostResponse updatePost(UUID userId, UUID postId, UpdatePostRequest req) {
        RecipePost post = requirePost(postId);
        requireOwnership(post, userId);
        post.setTitle(req.title());
        post.setDescription(req.description());
        post.setImageUrl(req.imageUrl());
        post = postRepository.save(post);
        return toResponse(post);
    }

    @Transactional
    public void deletePost(UUID userId, UUID postId) {
        RecipePost post = requirePost(postId);
        requireOwnership(post, userId);
        postRepository.delete(post);
    }

    // --- helpers ---

    private RecipePost requirePost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> CommunityException.notFound("Post not found"));
    }

    private static void requireOwnership(RecipePost post, UUID userId) {
        if (!post.getUserId().equals(userId)) {
            throw CommunityException.forbidden("You do not own this post");
        }
    }

    private static PostResponse toResponse(RecipePost p) {
        return new PostResponse(p.getId(), p.getUserId(), p.getTitle(),
                p.getDescription(), p.getImageUrl(), p.getCreatedAt());
    }

    private static PostDetailResponse toDetailResponse(RecipePost p, long likes, long comments) {
        return new PostDetailResponse(p.getId(), p.getUserId(), p.getTitle(),
                p.getDescription(), p.getImageUrl(), likes, comments, p.getCreatedAt());
    }
}
