package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    List<Quiz> findByNodeIdIn(List<UUID> nodeIds);

    List<Quiz> findByNodeIdOrderByCreatedAt(UUID nodeId);

    long countByNodeId(UUID nodeId);
}
