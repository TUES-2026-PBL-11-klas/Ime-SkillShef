package com.skillchef.server.user;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import com.skillchef.server.user.SocialDtos.FollowCountsResponse;
import com.skillchef.server.user.SocialDtos.FollowStatusResponse;
import com.skillchef.server.user.SocialDtos.PageResponse;
import com.skillchef.server.user.SocialDtos.UserSummary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * HTTP entrypoints for the Social Service: follow/unfollow, followers list,
 * following list, "is following" checks, and follower counts.
 */
@RestController
@RequestMapping("/api/social")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/follow/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void follow(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable UUID userId) {
        socialService.follow(requireUser(principal), userId);
    }

    @DeleteMapping("/follow/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable UUID userId) {
        socialService.unfollow(requireUser(principal), userId);
    }

    @GetMapping("/follow/{userId}/status")
    public FollowStatusResponse isFollowing(@AuthenticationPrincipal AuthPrincipal principal,
                                            @PathVariable UUID userId) {
        return socialService.isFollowing(requireUser(principal), userId);
    }

    @GetMapping("/users/{userId}/counts")
    public FollowCountsResponse counts(@PathVariable UUID userId) {
        return socialService.counts(userId);
    }

    @GetMapping("/users/{userId}/followers")
    public PageResponse<UserSummary> followers(@PathVariable UUID userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return socialService.followers(userId, page, size);
    }

    @GetMapping("/users/{userId}/following")
    public PageResponse<UserSummary> following(@PathVariable UUID userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return socialService.following(userId, page, size);
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }
}
