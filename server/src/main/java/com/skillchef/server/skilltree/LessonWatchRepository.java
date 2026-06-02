package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonWatchRepository extends JpaRepository<LessonWatch, LessonWatchId> {

    List<LessonWatch> findByIdUserIdAndIdLessonIdIn(UUID userId, List<UUID> lessonIds);

    boolean existsByIdUserIdAndIdLessonId(UUID userId, UUID lessonId);
}
