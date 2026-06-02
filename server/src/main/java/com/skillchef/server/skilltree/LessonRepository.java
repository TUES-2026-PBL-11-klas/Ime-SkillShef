package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findByNodeIdIn(List<UUID> nodeIds);

    List<Lesson> findByNodeIdOrderByCreatedAt(UUID nodeId);
}
