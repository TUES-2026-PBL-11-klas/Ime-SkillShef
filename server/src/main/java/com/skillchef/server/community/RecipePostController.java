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

/**
 * Recipe post CRUD.
 *
 * Image upload happens outside this controller via the storage layer:
 *   POST /api/storage/presign → PUT to S3/R2 → POST /api/storage/process
 * Pass the resulting CDN URL as {@code imageUrl} in the request body.
 */
@RestController
@RequestMapping("/api/posts")
public class RecipePostController {

    private final RecipeService recipeService;

    public RecipePostController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(
            @Valid @RequestBody CreatePostRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return recipeService.createPost(principal.userId(), req);
    }

    @GetMapping("/{postId}")
    public PostDetailResponse getPost(@PathVariable UUID postId) {
        return recipeService.getPost(postId);
    }

    /** List posts by a specific user, newest first (paginated). */
    @GetMapping
    public Page<PostResponse> getUserPosts(
            @RequestParam UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return recipeService.getUserPosts(userId, pageable);
    }

    @PutMapping("/{postId}")
    public PostResponse updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody UpdatePostRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return recipeService.updatePost(principal.userId(), postId, req);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        recipeService.deletePost(principal.userId(), postId);
    }
}
