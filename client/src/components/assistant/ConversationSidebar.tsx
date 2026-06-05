"use client";

import { cn } from "@/lib/utils";
import { conversationTitle } from "@/domain/ai";
import type { ConversationSummary } from "@/schemas/ai";

interface ConversationSidebarProps {
  conversations: ConversationSummary[];
  activeId: string | null;
  onSelect: (conversationId: string) => void;
  onNew: () => void;
  className?: string;
}

export function ConversationSidebar({
  conversations,
  activeId,
  onSelect,
  onNew,
  className,
}: ConversationSidebarProps) {
  return (
    <aside className={cn("flex flex-col gap-2 border-r border-gray-200 p-3", className)}>
      <button
        onClick={onNew}
        className="w-full rounded-lg bg-orange-500 px-3 py-2 text-sm font-medium text-white transition-colors hover:bg-orange-600"
      >
        + New chat
      </button>

      {conversations.length === 0 ? (
        <p className="px-1 pt-2 text-xs text-gray-400">No conversations yet.</p>
      ) : (
        <ul className="flex-1 space-y-1 overflow-y-auto">
          {conversations.map((conversation) => (
            <li key={conversation.id}>
              <button
                onClick={() => onSelect(conversation.id)}
                className={cn(
                  "w-full truncate rounded-lg px-3 py-2 text-left text-sm transition-colors",
                  conversation.id === activeId
                    ? "bg-orange-100 text-orange-800"
                    : "text-gray-700 hover:bg-gray-100",
                )}
                title={conversationTitle(conversation)}
              >
                {conversationTitle(conversation)}
              </button>
            </li>
          ))}
        </ul>
      )}
    </aside>
  );
}
