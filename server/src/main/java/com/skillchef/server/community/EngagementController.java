package com.skillchef.server.community;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.skillchef.server.community.RecipeDtos.*;

/** Like and comment endpoints nested under a recipe post. */
@RestController
@RequestMapping("/api/posts/{postId}")
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    // --- likes ---

    @PostMapping("/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        engagementService.likePost(principal.userId(), postId);
    }

    @DeleteMapping("/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        engagementService.unlikePost(principal.userId(), postId);
    }

    // --- engagement summary ---

    @GetMapping("/engagement")
    public EngagementSummary getEngagement(@PathVariable UUID postId) {
        return engagementService.getEngagementSummary(postId);
    }

    // --- comments ---

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return engagementService.addComment(principal.userId(), postId, req);
    }

    @GetMapping("/comments")
    public Page<CommentResponse> getComments(
            @PathVariable UUID postId,
            @PageableDefault(size = 20) Pageable pageable) {
        return engagementService.getComments(postId, pageable);
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return engagementService.updateComment(principal.userId(), postId, commentId, req);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        engagementService.deleteComment(principal.userId(), postId, commentId);
    }
}
