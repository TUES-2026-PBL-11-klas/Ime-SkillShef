package com.skillchef.server.skilltree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    List<Quiz> findByNodeIdIn(List<UUID> nodeIds);
}
