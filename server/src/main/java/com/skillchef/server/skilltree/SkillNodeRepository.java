package com.skillchef.server.skilltree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SkillNodeRepository extends JpaRepository<SkillNode, UUID> {

    List<SkillNode> findByDomainId(UUID domainId);

    long countByDomainIdAndTierLessThan(UUID domainId, int tier);

    @Query("select count(sp) from SkillProgress sp join SkillNode sn on sp.id.nodeId = sn.id "
            + "where sp.id.userId = :userId and sn.domainId = :domainId and sn.tier < :tier "
            + "and sp.completedAt is not null")
    long countCompletedByUserAndDomainAndTierLessThan(@Param("userId") UUID userId,
                                                      @Param("domainId") UUID domainId,
                                                      @Param("tier") int tier);
}
