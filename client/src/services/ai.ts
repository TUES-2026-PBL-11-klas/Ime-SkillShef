import * as aiApi from "@/external/ai";
import type { ApiResponse } from "@/schemas/api";
import type {
  ConversationDetail,
  ConversationSummary,
  SendMessageInput,
} from "@/schemas/ai";

/**
 * AI Chef Assistant use-cases. Thin orchestration over the external API; the
 * conversation/message business logic lives in the backend service.
 */

export async function listConversations(): Promise<ApiResponse<ConversationSummary[]>> {
  return aiApi.fetchConversations();
}

export async function getConversation(
  conversationId: string,
): Promise<ApiResponse<ConversationDetail>> {
  return aiApi.fetchConversation(conversationId);
}

export async function startConversation(
  input: SendMessageInput,
): Promise<ApiResponse<ConversationDetail>> {
  return aiApi.startConversation(input);
}

export async function sendMessage(
  conversationId: string,
  input: SendMessageInput,
): Promise<ApiResponse<ConversationDetail>> {
  return aiApi.sendMessage(conversationId, input);
}
