import {
  getConversationAction,
  listConversationsAction,
  sendMessageAction,
  startConversationAction,
} from "@/actions/ai";
import type { SendMessageInput } from "@/schemas/ai";

/**
 * Client-side wrappers over the AI assistant Server Actions. Thin and stable so
 * hooks in `src/client/state` have a clean API to call.
 */

export function listConversations() {
  return listConversationsAction();
}

export function getConversation(conversationId: string) {
  return getConversationAction(conversationId);
}

export function startConversation(input: SendMessageInput) {
  return startConversationAction(input);
}

export function sendMessage(conversationId: string, input: SendMessageInput) {
  return sendMessageAction(conversationId, input);
}
