package com.skillchef.server.skilltree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillProgressRepository extends JpaRepository<SkillProgress, SkillProgressId> {

    List<SkillProgress> findByIdUserId(UUID userId);
}
