package com.skillchef.server.ai;

import com.skillchef.server.ai.client.AiChatClient;
import com.skillchef.server.ai.client.AiChatMessage;
import com.skillchef.server.ai.client.AiChatRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.skillchef.server.ai.AiDtos.ConversationDetail;
import static com.skillchef.server.ai.AiDtos.ConversationSummary;
import static com.skillchef.server.ai.AiDtos.MessageResponse;

/**
 * Application logic for the AI Chef Assistant: persists conversations/messages
 * and drives the {@link AiChatClient} (Gemini) over the running history.
 *
 * <p>This sits on top of the transport client built earlier — it owns the
 * SkillChef persona/prompt and persistence; the client owns retries/rate-limit.
 */
@Service
public class AiAssistantService {

    static final String ROLE_USER = "user";
    static final String ROLE_ASSISTANT = "assistant";
    private static final int TITLE_MAX = 60;

    private static final String SYSTEM_PROMPT = """
            You are SkillChef's AI Chef Assistant, a friendly and encouraging culinary coach.
            Help home cooks build real skills: explain techniques clearly at a beginner-to-intermediate
            level, suggest recipes from the ingredients they have, propose ingredient substitutions, and
            give practical timing and organization tips. Keep answers concise and actionable. If a question
            is unrelated to cooking, gently steer the conversation back to the kitchen.""";

    private final AiConversationRepository conversationRepository;
    private final AiMessageRepository messageRepository;
    private final AiChatClient aiChatClient;

    public AiAssistantService(AiConversationRepository conversationRepository,
                              AiMessageRepository messageRepository,
                              AiChatClient aiChatClient) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.aiChatClient = aiChatClient;
    }

    @Transactional(readOnly = true)
    public List<ConversationSummary> listConversations(UUID userId) {
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(c -> new ConversationSummary(c.getId(), c.getTitle(), c.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversationDetail getConversation(UUID userId, UUID conversationId) {
        AiConversation conversation = requireOwnedConversation(userId, conversationId);
        List<MessageResponse> messages = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(m -> new MessageResponse(m.getId(), m.getRole(), m.getContent(), m.getCreatedAt()))
                .toList();
        return new ConversationDetail(conversation.getId(), conversation.getTitle(),
                conversation.getCreatedAt(), messages);
    }

    /** Create a new conversation seeded with the first user message and the AI reply. */
    @Transactional
    public UUID startConversation(UUID userId, String content) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setTitle(deriveTitle(content));
        conversation = conversationRepository.save(conversation);
        exchange(conversation.getId(), content);
        return conversation.getId();
    }

    /** Append a user message to an existing conversation and persist the AI reply. */
    @Transactional
    public void sendMessage(UUID userId, UUID conversationId, String content) {
        requireOwnedConversation(userId, conversationId);
        exchange(conversationId, content);
    }

    /**
     * Persists the user message, calls the AI over the full running history, and
     * persists the reply. The whole exchange is one transaction, so if the AI call
     * fails nothing is saved and the user can retry cleanly. (This does hold a DB
     * connection across the provider call — acceptable at MVP scale.)
     */
    private void exchange(UUID conversationId, String userContent) {
        AiMessage userMessage = new AiMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole(ROLE_USER);
        userMessage.setContent(userContent);
        messageRepository.save(userMessage);

        List<AiChatMessage> history = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(AiAssistantService::toChatMessage)
                .toList();

        String reply = aiChatClient.complete(AiChatRequest.of(SYSTEM_PROMPT, history)).content();

        AiMessage assistantMessage = new AiMessage();
        assistantMessage.setConversationId(conversationId);
        assistantMessage.setRole(ROLE_ASSISTANT);
        assistantMessage.setContent(reply);
        messageRepository.save(assistantMessage);
    }

    private AiConversation requireOwnedConversation(UUID userId, UUID conversationId) {
        AiConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> AiApiException.notFound("Conversation not found"));
        if (!conversation.getUserId().equals(userId)) {
            throw AiApiException.forbidden("You do not own this conversation");
        }
        return conversation;
    }

    private static AiChatMessage toChatMessage(AiMessage message) {
        return ROLE_ASSISTANT.equals(message.getRole())
                ? AiChatMessage.assistant(message.getContent())
                : AiChatMessage.user(message.getContent());
    }

    private static String deriveTitle(String content) {
        String trimmed = content.strip();
        if (trimmed.length() <= TITLE_MAX) {
            return trimmed;
        }
        return trimmed.substring(0, TITLE_MAX - 1).strip() + "…";
    }
}
