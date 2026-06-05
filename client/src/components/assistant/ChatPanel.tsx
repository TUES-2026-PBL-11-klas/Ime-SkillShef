"use client";

import { useEffect, useRef, useState } from "react";
import { cn } from "@/lib/utils";
import { isAssistant } from "@/domain/ai";
import type { AiMessage } from "@/schemas/ai";

interface ChatPanelProps {
  messages: AiMessage[];
  status: "idle" | "loading" | "sending";
  error: string | null;
  onSend: (content: string) => void;
}

export function ChatPanel({ messages, status, error, onSend }: ChatPanelProps) {
  const [draft, setDraft] = useState("");
  const endRef = useRef<HTMLDivElement>(null);

  // Keep the latest message (and the "thinking" indicator) in view.
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, status]);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const trimmed = draft.trim();
    if (!trimmed || status === "sending") return;
    onSend(trimmed);
    setDraft("");
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  }

  const showEmptyState = messages.length === 0 && status !== "loading";

  return (
    <div className="flex flex-1 flex-col">
      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        {status === "loading" && (
          <p className="text-center text-sm text-gray-400">Loading conversation…</p>
        )}

        {showEmptyState && (
          <div className="flex h-full flex-col items-center justify-center text-center text-gray-400">
            <p className="text-3xl">👨‍🍳</p>
            <p className="mt-2 text-sm">
              Ask the AI Chef Assistant anything — techniques, recipes from your
              ingredients, substitutions, or timing help.
            </p>
          </div>
        )}

        {messages.map((message) => (
          <MessageBubble key={message.id} message={message} />
        ))}

        {status === "sending" && <ThinkingBubble />}

        <div ref={endRef} />
      </div>

      {error && (
        <p className="px-4 pb-2 text-sm text-red-500" role="alert">
          {error}
        </p>
      )}

      <form onSubmit={handleSubmit} className="flex items-end gap-2 border-t border-gray-200 p-3">
        <textarea
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Ask the chef…"
          rows={1}
          maxLength={4000}
          className="max-h-40 flex-1 resize-none rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-orange-400 focus:outline-none"
        />
        <button
          type="submit"
          disabled={status === "sending" || !draft.trim()}
          className="rounded-lg bg-orange-500 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-orange-600 disabled:opacity-50"
        >
          Send
        </button>
      </form>
    </div>
  );
}

function MessageBubble({ message }: { message: AiMessage }) {
  const assistant = isAssistant(message.role);
  return (
    <div className={cn("flex", assistant ? "justify-start" : "justify-end")}>
      <div
        className={cn(
          "max-w-[80%] whitespace-pre-wrap rounded-2xl px-4 py-2 text-sm",
          assistant
            ? "rounded-tl-sm bg-gray-100 text-gray-800"
            : "rounded-tr-sm bg-orange-500 text-white",
        )}
      >
        {message.content}
      </div>
    </div>
  );
}

function ThinkingBubble() {
  return (
    <div className="flex justify-start">
      <div className="flex gap-1 rounded-2xl rounded-tl-sm bg-gray-100 px-4 py-3">
        <Dot delay="0ms" />
        <Dot delay="150ms" />
        <Dot delay="300ms" />
      </div>
    </div>
  );
}

function Dot({ delay }: { delay: string }) {
  return (
    <span
      className="h-2 w-2 animate-bounce rounded-full bg-gray-400"
      style={{ animationDelay: delay }}
    />
  );
}
