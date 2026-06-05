/**
 * Pure domain helpers for the AI assistant. No side effects.
 */

export const ASSISTANT_ROLE = "assistant";
export const USER_ROLE = "user";

export function isAssistant(role: string): boolean {
  return role === ASSISTANT_ROLE;
}

/** Display title for a conversation, with a fallback for untitled ones. */
export function conversationTitle(conversation: { title: string | null }): string {
  const title = conversation.title?.trim();
  return title && title.length > 0 ? title : "New chat";
}
