package com.skillchef.server.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiConversationRepository extends JpaRepository<AiConversation, UUID> {

    /** A user's conversations, newest first (uses idx_ai_conversations_user_id). */
    List<AiConversation> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
