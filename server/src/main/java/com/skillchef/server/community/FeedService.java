package com.skillchef.server.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.skillchef.server.community.RecipeDtos.PostDetailResponse;

/**
 * Community feed queries.
 *
 * Two sort modes are available via {@link FeedSort}:
 *   RECENT   — posts from followed accounts, newest first (personalised)
 *   TRENDING — all posts sorted by like count in the last 24 hours (discovery)
 *
 * Per-post engagement counts (likes + comments) are fetched alongside each
 * page. For MVP traffic this is acceptable; a projection query can eliminate
 * the N+1 if feed pages grow large.
 */
@Service
public class FeedService {

    private final RecipePostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public FeedService(RecipePostRepository postRepository,
                       LikeRepository likeRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Returns posts from accounts the authenticated user follows, newest first.
     * Returns an empty page when the user follows nobody.
     */
    @Transactional(readOnly = true)
    public Page<PostDetailResponse> getFollowingFeed(UUID userId, Pageable pageable) {
        return postRepository.findFollowingFeed(userId, pageable)
                .map(this::toDetail);
    }

    /**
     * Returns all posts sorted by like count in the last 24 hours descending.
     * Does not require authentication — suitable for unauthenticated discovery.
     * The sort order is fixed; only page/size from {@code pageable} are applied.
     */
    @Transactional(readOnly = true)
    public Page<PostDetailResponse> getTrendingFeed(Pageable pageable) {
        // Strip any caller-supplied sort to avoid conflicting with the embedded ORDER BY.
        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return postRepository.findTrendingFeed(unsorted)
                .map(this::toDetail);
    }

    private PostDetailResponse toDetail(RecipePost post) {
        // TODO: replace with a single projection query if feed page sizes exceed ~50 posts
        long likes    = likeRepository.countByRecipePostId(post.getId());
        long comments = commentRepository.countByRecipePostId(post.getId());
        return new PostDetailResponse(
                post.getId(), post.getUserId(), post.getTitle(),
                post.getDescription(), post.getImageUrl(),
                likes, comments, post.getCreatedAt());
    }
}
