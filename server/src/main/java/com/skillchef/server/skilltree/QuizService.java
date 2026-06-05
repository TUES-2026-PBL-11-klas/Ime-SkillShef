package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizPublicDto;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizResultResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.QuizUpdateRequest;
import com.skillchef.server.user.User;
import com.skillchef.server.user.UserRepository;

/**
 * Quiz management, delivery and scoring (issue #15).
 *
 * <p>Delivery never exposes the correct answer (see {@link QuizPublicDto}).
 * The first correct submission per (user, quiz) awards XP via
 * {@link XpAwardService} (writing {@code xp_transactions} and updating
 * {@code users.global_xp}/{@code level}) and advances the node's
 * {@code skill_progress}.
 */
@Service
public class QuizService {

    /** Flat XP granted the first time a user passes a quiz. */
    static final int QUIZ_PASS_XP = 25;

    private final QuizRepository quizRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SkillProgressRepository skillProgressRepository;
    private final XpAwardService xpAwardService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public QuizService(QuizRepository quizRepository,
                       SkillNodeRepository skillNodeRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       SkillProgressRepository skillProgressRepository,
                       XpAwardService xpAwardService,
                       UserRepository userRepository,
                       ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.skillProgressRepository = skillProgressRepository;
        this.xpAwardService = xpAwardService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // ----- CRUD --------------------------------------------------------------

    @Transactional
    public QuizPublicDto create(QuizRequest request) {
        requireNode(request.nodeId());

        Quiz quiz = new Quiz();
        quiz.setNodeId(request.nodeId());
        quiz.setQuestion(request.question());
        quiz.setOptions(serializeOptions(request.options()));
        quiz.setCorrectAnswer(request.correctAnswer());
        Quiz saved = quizRepository.save(quiz);

        return toPublicDto(saved, false);
    }

    @Transactional
    public QuizPublicDto update(UUID quizId, QuizUpdateRequest request) {
        Quiz quiz = requireQuiz(quizId);
        quiz.setQuestion(request.question());
        quiz.setOptions(serializeOptions(request.options()));
        quiz.setCorrectAnswer(request.correctAnswer());
        Quiz saved = quizRepository.save(quiz);
        return toPublicDto(saved, false);
    }

    @Transactional
    public void delete(UUID quizId) {
        Quiz quiz = requireQuiz(quizId);
        quizRepository.delete(quiz);
    }

    // ----- Delivery (no correct answer) --------------------------------------

    @Transactional(readOnly = true)
    public QuizPublicDto getPublic(UUID userId, UUID quizId) {
        Quiz quiz = requireQuiz(quizId);
        boolean passed = quizAttemptRepository.existsByUserIdAndQuizIdAndCorrectIsTrue(userId, quizId);
        return toPublicDto(quiz, passed);
    }

    @Transactional(readOnly = true)
    public List<QuizPublicDto> listByNode(UUID userId, UUID nodeId) {
        requireNode(nodeId);
        List<Quiz> quizzes = quizRepository.findByNodeIdOrderByCreatedAt(nodeId);
        if (quizzes.isEmpty()) {
            return List.of();
        }

        Set<UUID> passed = quizzes.stream()
                .filter(q -> quizAttemptRepository.existsByUserIdAndQuizIdAndCorrectIsTrue(userId, q.getId()))
                .map(Quiz::getId)
                .collect(Collectors.toCollection(HashSet::new));

        return quizzes.stream()
                .map(quiz -> toPublicDto(quiz, passed.contains(quiz.getId())))
                .toList();
    }

    // ----- Scoring -----------------------------------------------------------

    @CacheEvict(value = "skillTree", key = "#userId")
    @Transactional
    public QuizResultResponse submit(UUID userId, UUID quizId, String selectedAnswer) {
        Quiz quiz = requireQuiz(quizId);

        boolean correct = quiz.getCorrectAnswer() != null
                && quiz.getCorrectAnswer().trim().equalsIgnoreCase(selectedAnswer.trim());
        boolean alreadyPassed = quizAttemptRepository
                .existsByUserIdAndQuizIdAndCorrectIsTrue(userId, quizId);
        boolean firstPass = correct && !alreadyPassed;
        int awardedXp = firstPass ? QUIZ_PASS_XP : 0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(userId);
        attempt.setQuizId(quizId);
        attempt.setSelectedAnswer(selectedAnswer);
        attempt.setCorrect(correct);
        attempt.setAwardedXp(awardedXp);
        quizAttemptRepository.save(attempt);

        int newGlobalXp;
        int newLevel;
        if (firstPass) {
            XpAwardService.Result result =
                    xpAwardService.award(userId, awardedXp, "quiz_passed", "QUIZ", quizId);
            newGlobalXp = result.newGlobalXp();
            newLevel = result.newLevel();
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            newGlobalXp = user.getGlobalXp();
            newLevel = user.getLevel();
        }

        int nodeProgressPercent = updateNodeProgress(userId, quiz.getNodeId());

        return new QuizResultResponse(quizId, correct, firstPass, awardedXp,
                newGlobalXp, newLevel, nodeProgressPercent);
    }

    /**
     * Recomputes {@code skill_progress.progress_percent} for the node as the
     * share of its quizzes the user has passed, upserting the row. Sets
     * {@code unlocked_at} on first interaction and {@code completed_at} when all
     * quizzes are passed.
     *
     * @return the resulting progress percentage
     */
    private int updateNodeProgress(UUID userId, UUID nodeId) {
        long totalQuizzes = quizRepository.countByNodeId(nodeId);
        long passedQuizzes = quizAttemptRepository.countPassedQuizzesByUserAndNode(userId, nodeId);

        int percent = totalQuizzes == 0
                ? 0
                : (int) Math.round((passedQuizzes * 100.0) / totalQuizzes);

        SkillProgressId id = new SkillProgressId(userId, nodeId);
        SkillProgress progress = skillProgressRepository.findById(id)
                .orElseGet(() -> {
                    SkillProgress fresh = new SkillProgress();
                    fresh.setId(id);
                    return fresh;
                });

        OffsetDateTime now = OffsetDateTime.now();
        if (progress.getUnlockedAt() == null) {
            progress.setUnlockedAt(now);
        }
        progress.setProgressPercent(percent);
        if (percent >= 100 && progress.getCompletedAt() == null) {
            progress.setCompletedAt(now);
        }
        skillProgressRepository.save(progress);

        return percent;
    }

    // ----- helpers -----------------------------------------------------------

    private void requireNode(UUID nodeId) {
        if (!skillNodeRepository.existsById(nodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill node not found");
        }
    }

    private Quiz requireQuiz(UUID quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
    }

    private String serializeOptions(List<String> options) {
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quiz options");
        }
    }

    private List<String> parseOptions(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Corrupt quiz options");
        }
    }

    private QuizPublicDto toPublicDto(Quiz quiz, boolean passed) {
        return new QuizPublicDto(
                quiz.getId(),
                quiz.getNodeId(),
                quiz.getQuestion(),
                parseOptions(quiz.getOptions()),
                passed);
    }
}
