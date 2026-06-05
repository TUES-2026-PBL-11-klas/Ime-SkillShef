package com.skillchef.server.ai;

import com.skillchef.server.ai.client.AiChatClient;
import com.skillchef.server.ai.client.AiChatRequest;
import com.skillchef.server.ai.client.AiChatResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAssistantServiceTest {

    @Mock
    private AiConversationRepository conversationRepository;
    @Mock
    private AiMessageRepository messageRepository;
    @Mock
    private AiChatClient aiChatClient;

    @InjectMocks
    private AiAssistantService service;

    @Test
    void startConversationPersistsUserAndAssistantMessagesAndCallsAi() {
        UUID userId = UUID.randomUUID();
        when(conversationRepository.save(any(AiConversation.class))).thenAnswer(inv -> {
            AiConversation c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });
        when(messageRepository.save(any(AiMessage.class))).thenAnswer(inv -> inv.getArgument(0));
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(any()))
                .thenAnswer(inv -> List.of(userMessage("How do I dice an onion?")));
        when(aiChatClient.complete(any(AiChatRequest.class)))
                .thenReturn(new AiChatResult("Cut in half, then make horizontal and vertical cuts.", 10, 12));

        UUID conversationId = service.startConversation(userId, "How do I dice an onion?");

        assertThat(conversationId).isNotNull();

        // The conversation title is derived from the first message.
        ArgumentCaptor<AiConversation> conv = ArgumentCaptor.forClass(AiConversation.class);
        verify(conversationRepository).save(conv.capture());
        assertThat(conv.getValue().getUserId()).isEqualTo(userId);
        assertThat(conv.getValue().getTitle()).isEqualTo("How do I dice an onion?");

        // Both the user message and the assistant reply are persisted with correct roles.
        ArgumentCaptor<AiMessage> messages = ArgumentCaptor.forClass(AiMessage.class);
        verify(messageRepository, org.mockito.Mockito.times(2)).save(messages.capture());
        List<AiMessage> saved = messages.getAllValues();
        assertThat(saved.get(0).getRole()).isEqualTo(AiAssistantService.ROLE_USER);
        assertThat(saved.get(0).getContent()).isEqualTo("How do I dice an onion?");
        assertThat(saved.get(1).getRole()).isEqualTo(AiAssistantService.ROLE_ASSISTANT);
        assertThat(saved.get(1).getContent()).isEqualTo("Cut in half, then make horizontal and vertical cuts.");
    }

    @Test
    void sendMessageToForeignConversationIsForbiddenAndDoesNotCallAi() {
        UUID owner = UUID.randomUUID();
        UUID attacker = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        AiConversation conversation = new AiConversation();
        conversation.setUserId(owner);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> service.sendMessage(attacker, conversationId, "hi"))
                .isInstanceOf(AiApiException.class);

        verifyNoInteractions(aiChatClient);
    }

    @Test
    void getConversationNotFoundThrows() {
        UUID userId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getConversation(userId, conversationId))
                .isInstanceOf(AiApiException.class);
    }

    private static AiMessage userMessage(String content) {
        AiMessage m = new AiMessage();
        m.setConversationId(UUID.randomUUID());
        m.setRole(AiAssistantService.ROLE_USER);
        m.setContent(content);
        return m;
    }
}
