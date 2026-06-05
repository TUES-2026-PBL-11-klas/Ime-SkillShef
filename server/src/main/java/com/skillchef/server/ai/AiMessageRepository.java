package com.skillchef.server.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiMessageRepository extends JpaRepository<AiMessage, UUID> {

    /** A conversation's messages in chronological order (uses idx_ai_messages_conversation). */
    List<AiMessage> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);
}
