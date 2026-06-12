package com.skillchef.server.ai;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.skillchef.server.ai.AiDtos.*;

/**
 * AI Chef Assistant chat endpoints.
 *
 * <p>Write endpoints persist the exchange and then re-read via
 * {@link AiAssistantService#getConversation} (a fresh read transaction) so the
 * returned timestamps reflect the database-generated {@code created_at}.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiAssistantService assistantService;

    public AiController(AiAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/conversations")
    public List<ConversationSummary> listConversations(@AuthenticationPrincipal AuthPrincipal principal) {
        return assistantService.listConversations(principal.userId());
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationDetail startConversation(
            @Valid @RequestBody SendMessageRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        UUID conversationId = assistantService.startConversation(principal.userId(), req.content());
        return assistantService.getConversation(principal.userId(), conversationId);
    }

    @GetMapping("/conversations/{conversationId}")
    public ConversationDetail getConversation(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return assistantService.getConversation(principal.userId(), conversationId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ConversationDetail sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        assistantService.sendMessage(principal.userId(), conversationId, req.content());
        return assistantService.getConversation(principal.userId(), conversationId);
    }
}
