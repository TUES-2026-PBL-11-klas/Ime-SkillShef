import { z } from "zod";

/**
 * Single source of truth for AI Chef Assistant types/validation.
 * Mirrors the backend AiDtos (`/api/ai`).
 */

export const AiMessageSchema = z.object({
  id: z.string(),
  role: z.string(),
  content: z.string(),
  createdAt: z.string(),
});
export type AiMessage = z.infer<typeof AiMessageSchema>;

export const ConversationSummarySchema = z.object({
  id: z.string(),
  title: z.string().nullable(),
  createdAt: z.string(),
});
export type ConversationSummary = z.infer<typeof ConversationSummarySchema>;

export const ConversationListSchema = z.array(ConversationSummarySchema);

export const ConversationDetailSchema = z.object({
  id: z.string(),
  title: z.string().nullable(),
  createdAt: z.string(),
  messages: z.array(AiMessageSchema),
});
export type ConversationDetail = z.infer<typeof ConversationDetailSchema>;

export const SendMessageInputSchema = z.object({
  content: z
    .string()
    .trim()
    .min(1, "Message cannot be empty")
    .max(4000, "Message is too long (max 4000 characters)"),
});
export type SendMessageInput = z.infer<typeof SendMessageInputSchema>;
