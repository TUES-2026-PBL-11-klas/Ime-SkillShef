package com.skillchef.server.skilltree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillDomainRepository extends JpaRepository<SkillDomain, UUID> {
}
