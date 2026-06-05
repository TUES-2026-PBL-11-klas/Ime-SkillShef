package com.skillchef.server.community;

import com.skillchef.server.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.skillchef.server.community.RecipeDtos.*;

/** Application logic for likes and comments on recipe posts. */
@Service
public class EngagementService {

    private static final Logger log = LoggerFactory.getLogger(EngagementService.class);

    private final RecipePostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public EngagementService(RecipePostRepository postRepository,
                              LikeRepository likeRepository,
                              CommentRepository commentRepository,
                              NotificationService notificationService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    // --- likes ---

    @Transactional
    public void likePost(UUID userId, UUID postId) {
        requirePostExists(postId);
        if (likeRepository.existsByUserIdAndRecipePostId(userId, postId)) {
            return; // idempotent — already liked
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setRecipePostId(postId);
        likeRepository.save(like);
        // best-effort: notification failure must not roll back the like
        try { notificationService.onPostLiked(userId, postId); }
        catch (Exception e) { log.warn("Like notification failed for post {}: {}", postId, e.getMessage()); }
    }

    @Transactional
    public void unlikePost(UUID userId, UUID postId) {
        likeRepository.findByUserIdAndRecipePostId(userId, postId)
                .ifPresent(likeRepository::delete);
        // idempotent — no error when not liked
    }

    // --- comments ---

    @Transactional
    public CommentResponse addComment(UUID userId, UUID postId, CommentRequest req) {
        requirePostExists(postId);
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setRecipePostId(postId);
        comment.setContent(req.content());
        comment = commentRepository.save(comment);
        // best-effort: notification failure must not roll back the comment
        try { notificationService.onPostCommented(userId, postId); }
        catch (Exception e) { log.warn("Comment notification failed for post {}: {}", postId, e.getMessage()); }
        return toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(UUID postId, Pageable pageable) {
        requirePostExists(postId);
        return commentRepository.findByRecipePostIdOrderByCreatedAtAsc(postId, pageable)
                .map(EngagementService::toCommentResponse);
    }

    @Transactional
    public CommentResponse updateComment(UUID userId, UUID postId, UUID commentId, CommentRequest req) {
        Comment comment = requireComment(commentId, postId);
        requireCommentOwnership(comment, userId);
        comment.setContent(req.content());
        comment = commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    @Transactional
    public void deleteComment(UUID userId, UUID postId, UUID commentId) {
        Comment comment = requireComment(commentId, postId);
        requireCommentOwnership(comment, userId);
        commentRepository.delete(comment);
    }

    // --- engagement summary ---

    @Transactional(readOnly = true)
    public EngagementSummary getEngagementSummary(UUID postId) {
        requirePostExists(postId);
        long likes    = likeRepository.countByRecipePostId(postId);
        long comments = commentRepository.countByRecipePostId(postId);
        return new EngagementSummary(likes, comments);
    }

    // --- helpers ---

    private void requirePostExists(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw CommunityException.notFound("Post not found");
        }
    }

    private Comment requireComment(UUID commentId, UUID postId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> CommunityException.notFound("Comment not found"));
        if (!comment.getRecipePostId().equals(postId)) {
            throw CommunityException.notFound("Comment not found on this post");
        }
        return comment;
    }

    private static void requireCommentOwnership(Comment comment, UUID userId) {
        if (!comment.getUserId().equals(userId)) {
            throw CommunityException.forbidden("You do not own this comment");
        }
    }

    private static CommentResponse toCommentResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getUserId(), c.getRecipePostId(),
                c.getContent(), c.getCreatedAt());
    }
}
