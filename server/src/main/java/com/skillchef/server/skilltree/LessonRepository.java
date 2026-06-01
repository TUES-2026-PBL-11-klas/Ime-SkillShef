package com.skillchef.server.skilltree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findByNodeIdIn(List<UUID> nodeIds);
}
