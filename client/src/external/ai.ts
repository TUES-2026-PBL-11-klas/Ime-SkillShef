import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  ConversationDetailSchema,
  ConversationListSchema,
  type ConversationDetail,
  type ConversationSummary,
  type SendMessageInput,
} from "@/schemas/ai";

/**
 * Backend integration for the AI Chef Assistant API (`/api/ai`). Each function
 * maps 1:1 to a backend endpoint.
 */

export async function fetchConversations() {
  return http<ConversationSummary[]>({
    method: "GET",
    path: "/api/ai/conversations",
    options: { headers: await getAuthHeaders() },
    schema: ConversationListSchema,
  });
}

export async function fetchConversation(conversationId: string) {
  return http<ConversationDetail>({
    method: "GET",
    path: `/api/ai/conversations/${conversationId}`,
    options: { headers: await getAuthHeaders() },
    schema: ConversationDetailSchema,
  });
}

export async function startConversation(input: SendMessageInput) {
  return http<ConversationDetail>({
    method: "POST",
    path: "/api/ai/conversations",
    options: { headers: await getAuthHeaders(), body: input },
    schema: ConversationDetailSchema,
  });
}

export async function sendMessage(conversationId: string, input: SendMessageInput) {
  return http<ConversationDetail>({
    method: "POST",
    path: `/api/ai/conversations/${conversationId}/messages`,
    options: { headers: await getAuthHeaders(), body: input },
    schema: ConversationDetailSchema,
  });
}
