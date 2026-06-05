"use client";

import { useAiAssistant } from "@/client/state/use-ai-assistant";
import type { ConversationSummary } from "@/schemas/ai";
import { ConversationSidebar } from "./ConversationSidebar";
import { ChatPanel } from "./ChatPanel";

interface AssistantExperienceProps {
  initialConversations: ConversationSummary[];
}

export function AssistantExperience({ initialConversations }: AssistantExperienceProps) {
  const {
    conversations,
    activeId,
    messages,
    status,
    error,
    openConversation,
    newConversation,
    send,
  } = useAiAssistant(initialConversations);

  return (
    <div className="flex h-[70vh] overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <ConversationSidebar
        className="hidden w-64 shrink-0 md:flex"
        conversations={conversations}
        activeId={activeId}
        onSelect={openConversation}
        onNew={newConversation}
      />
      <ChatPanel messages={messages} status={status} error={error} onSend={send} />
    </div>
  );
}
