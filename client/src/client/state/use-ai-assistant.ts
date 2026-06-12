"use client";

import { useCallback, useState } from "react";
import * as aiActions from "@/client/actions/ai";
import type {
  AiMessage,
  ConversationDetail,
  ConversationSummary,
} from "@/schemas/ai";

type ChatStatus = "idle" | "loading" | "sending";

/**
 * UI orchestration for the AI Chef Assistant chat. Owns the conversation list,
 * the active conversation's messages, and the send flow (with an optimistic
 * user message while the assistant is "thinking").
 */
export function useAiAssistant(initialConversations: ConversationSummary[]) {
  const [conversations, setConversations] =
    useState<ConversationSummary[]>(initialConversations);
  const [activeId, setActiveId] = useState<string | null>(null);
  const [messages, setMessages] = useState<AiMessage[]>([]);
  const [status, setStatus] = useState<ChatStatus>("idle");
  const [error, setError] = useState<string | null>(null);

  const upsertConversation = useCallback((detail: ConversationDetail) => {
    const summary: ConversationSummary = {
      id: detail.id,
      title: detail.title,
      createdAt: detail.createdAt,
    };
    setConversations((prev) => {
      const exists = prev.some((c) => c.id === detail.id);
      return exists
        ? prev.map((c) => (c.id === detail.id ? summary : c))
        : [summary, ...prev];
    });
  }, []);

  const openConversation = useCallback(async (conversationId: string) => {
    setActiveId(conversationId);
    setMessages([]);
    setError(null);
    setStatus("loading");
    const res = await aiActions.getConversation(conversationId);
    if (!res.success) {
      setError(res.error);
      setStatus("idle");
      return;
    }
    setMessages(res.data.messages);
    setStatus("idle");
  }, []);

  const newConversation = useCallback(() => {
    setActiveId(null);
    setMessages([]);
    setError(null);
    setStatus("idle");
  }, []);

  const send = useCallback(
    async (content: string) => {
      const trimmed = content.trim();
      if (!trimmed || status === "sending") return;

      setError(null);
      setStatus("sending");

      const optimistic: AiMessage = {
        id: `pending-${Date.now()}`,
        role: "user",
        content: trimmed,
        createdAt: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, optimistic]);

      const res = activeId
        ? await aiActions.sendMessage(activeId, { content: trimmed })
        : await aiActions.startConversation({ content: trimmed });

      if (!res.success) {
        // Drop the optimistic message so the user can edit/retry.
        setMessages((prev) => prev.filter((m) => m.id !== optimistic.id));
        setError(res.error);
        setStatus("idle");
        return;
      }

      setMessages(res.data.messages);
      setActiveId(res.data.id);
      upsertConversation(res.data);
      setStatus("idle");
    },
    [activeId, status, upsertConversation],
  );

  return {
    conversations,
    activeId,
    messages,
    status,
    error,
    openConversation,
    newConversation,
    send,
  };
}
