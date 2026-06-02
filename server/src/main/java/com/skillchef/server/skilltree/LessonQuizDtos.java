package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Request/response payloads for the Lesson and Quiz services (issue #15).
 *
 * <p>Note on security: the public quiz delivery DTO ({@link QuizPublicDto})
 * deliberately omits the correct answer so it is never exposed to clients.
 */
public final class LessonQuizDtos {

    private LessonQuizDtos() {
    }

    // ----- Lessons -----------------------------------------------------------

    public record LessonRequest(@NotNull UUID nodeId,
                                @NotBlank String title,
                                String description,
                                @NotBlank String videoUrl,
                                @Min(0) int durationSeconds) {
    }

    public record LessonUpdateRequest(@NotBlank String title,
                                      String description,
                                      @NotBlank String videoUrl,
                                      @Min(0) int durationSeconds) {
    }

    /** Lesson detail including whether the requesting user has watched it. */
    public record LessonResponse(UUID id,
                                 UUID nodeId,
                                 String title,
                                 String description,
                                 String videoUrl,
                                 int durationSeconds,
                                 boolean watched,
                                 OffsetDateTime watchedAt) {
    }

    /** Playback payload returning the Mux/CDN video URL for a lesson. */
    public record LessonPlaybackResponse(UUID lessonId,
                                         String videoUrl,
                                         int durationSeconds) {
    }

    public record WatchResponse(UUID lessonId,
                                boolean watched,
                                OffsetDateTime watchedAt) {
    }

    // ----- Quizzes -----------------------------------------------------------

    public record QuizRequest(@NotNull UUID nodeId,
                              @NotBlank String question,
                              @NotEmpty List<String> options,
                              @NotBlank String correctAnswer) {
    }

    public record QuizUpdateRequest(@NotBlank String question,
                                    @NotEmpty List<String> options,
                                    @NotBlank String correctAnswer) {
    }

    /**
     * Quiz as delivered to a learner. Intentionally does NOT include the
     * correct answer.
     */
    public record QuizPublicDto(UUID id,
                                UUID nodeId,
                                String question,
                                List<String> options,
                                boolean passed) {
    }

    public record QuizSubmissionRequest(@NotBlank String selectedAnswer) {
    }

    /** Result of a quiz submission, including any XP awarded and node progress. */
    public record QuizResultResponse(UUID quizId,
                                     boolean correct,
                                     boolean firstPass,
                                     int awardedXp,
                                     int newGlobalXp,
                                     int newLevel,
                                     int nodeProgressPercent) {
    }
}
