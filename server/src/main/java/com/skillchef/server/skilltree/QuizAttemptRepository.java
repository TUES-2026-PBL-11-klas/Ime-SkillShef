package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    List<QuizAttempt> findByUserIdAndQuizIdOrderByCreatedAtDesc(UUID userId, UUID quizId);

    boolean existsByUserIdAndQuizIdAndCorrectIsTrue(UUID userId, UUID quizId);

    /**
     * Number of distinct quizzes the user has passed within a given skill node.
     * Used to compute {@code progress_percent} for the node.
     */
    @Query("select count(distinct qa.quizId) from QuizAttempt qa join Quiz q on qa.quizId = q.id "
            + "where qa.userId = :userId and q.nodeId = :nodeId and qa.correct = true")
    long countPassedQuizzesByUserAndNode(@Param("userId") UUID userId,
                                         @Param("nodeId") UUID nodeId);
}
