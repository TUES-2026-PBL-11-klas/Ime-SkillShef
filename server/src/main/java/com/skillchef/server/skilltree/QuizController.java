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
import com.skillchef.server.skilltree.LessonQuizDtos.QuizPublicDto;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizResultResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizSubmissionRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizUpdateRequest;

import jakarta.validation.Valid;

/**
 * Quiz endpoints (issue #15): CRUD plus learner delivery and submission.
 *
 * <p>Delivery endpoints ({@code GET}) never return the correct answer.
 * Authoring endpoints are guarded by authentication only (single
 * {@code ROLE_USER} model); role-based authoring can be added later.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizPublicDto> create(@AuthenticationPrincipal AuthPrincipal principal,
                                                @Valid @RequestBody QuizRequest request) {
        requireUser(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.create(request));
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizPublicDto> update(@AuthenticationPrincipal AuthPrincipal principal,
                                                @PathVariable UUID quizId,
                                                @Valid @RequestBody QuizUpdateRequest request) {
        requireUser(principal);
        return ResponseEntity.ok(quizService.update(quizId, request));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal principal,
                                       @PathVariable UUID quizId) {
        requireUser(principal);
        quizService.delete(quizId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{quizId}")
    public QuizPublicDto get(@AuthenticationPrincipal AuthPrincipal principal,
                             @PathVariable UUID quizId) {
        UUID userId = requireUser(principal);
        return quizService.getPublic(userId, quizId);
    }

    @GetMapping("/node/{nodeId}")
    public List<QuizPublicDto> listByNode(@AuthenticationPrincipal AuthPrincipal principal,
                                          @PathVariable UUID nodeId) {
        UUID userId = requireUser(principal);
        return quizService.listByNode(userId, nodeId);
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submit(@AuthenticationPrincipal AuthPrincipal principal,
                                                     @PathVariable UUID quizId,
                                                     @Valid @RequestBody QuizSubmissionRequest request) {
        UUID userId = requireUser(principal);
        return ResponseEntity.ok(quizService.submit(userId, quizId, request.selectedAnswer()));
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }
}
