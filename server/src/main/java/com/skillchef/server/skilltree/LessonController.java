package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonPlaybackResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonUpdateRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.WatchResponse;

import jakarta.validation.Valid;

/**
 * Lesson endpoints (issue #15): CRUD, per-node listing with watched overlay,
 * playback URL, and watched tracking.
 *
 * <p>Authoring endpoints are guarded by authentication only, since the project
 * currently models a single {@code ROLE_USER}; role-based authoring can be
 * layered on once an admin role exists.
 */
@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<LessonResponse> create(@AuthenticationPrincipal AuthPrincipal principal,
                                                 @Valid @RequestBody LessonRequest request) {
        requireUser(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(request));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> update(@AuthenticationPrincipal AuthPrincipal principal,
                                                 @PathVariable UUID lessonId,
                                                 @Valid @RequestBody LessonUpdateRequest request) {
        requireUser(principal);
        return ResponseEntity.ok(lessonService.update(lessonId, request));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal principal,
                                       @PathVariable UUID lessonId) {
        requireUser(principal);
        lessonService.delete(lessonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lessonId}")
    public LessonResponse get(@AuthenticationPrincipal AuthPrincipal principal,
                              @PathVariable UUID lessonId) {
        UUID userId = requireUser(principal);
        return lessonService.get(userId, lessonId);
    }

    @GetMapping("/node/{nodeId}")
    public List<LessonResponse> listByNode(@AuthenticationPrincipal AuthPrincipal principal,
                                           @PathVariable UUID nodeId) {
        UUID userId = requireUser(principal);
        return lessonService.listByNode(userId, nodeId);
    }

    @GetMapping("/{lessonId}/playback")
    public LessonPlaybackResponse playback(@AuthenticationPrincipal AuthPrincipal principal,
                                           @PathVariable UUID lessonId) {
        requireUser(principal);
        return lessonService.getPlayback(lessonId);
    }

    @PostMapping("/{lessonId}/watch")
    public ResponseEntity<WatchResponse> markWatched(@AuthenticationPrincipal AuthPrincipal principal,
                                                     @PathVariable UUID lessonId) {
        UUID userId = requireUser(principal);
        return ResponseEntity.ok(lessonService.markWatched(userId, lessonId));
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }
}
