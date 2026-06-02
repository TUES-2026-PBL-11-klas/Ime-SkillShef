package com.skillchef.server.skilltree;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpTransactionRepository extends JpaRepository<XpTransaction, UUID> {

    List<XpTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
