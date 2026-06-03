package com.skillchef.server.community;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.skillchef.server.community.RecipeDtos.PostDetailResponse;

/**
 * Community feed endpoints.
 *
 * GET /api/feed?sort=RECENT    — personalised following feed (JWT required)
 * GET /api/feed?sort=TRENDING  — trending feed by 24h likes (public)
 *
 * Pagination: {@code ?page=0&size=20} (default size 20).
 */
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public Page<PostDetailResponse> getFeed(
            @RequestParam(defaultValue = "RECENT") FeedSort sort,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal AuthPrincipal principal) {

        return switch (sort) {
            case TRENDING -> feedService.getTrendingFeed(pageable);
            case RECENT -> {
                if (principal == null) {
                    throw new FeedAuthException();
                }
                yield feedService.getFollowingFeed(principal.userId(), pageable);
            }
        };
    }

    /** Thrown when RECENT feed is requested without a valid JWT. */
    static class FeedAuthException extends RuntimeException {
        FeedAuthException() {
            super("Authentication required for the personalised feed — use sort=TRENDING for public access");
        }
    }
}
