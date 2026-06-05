"use server";

import { z } from "zod";
import type { ApiResponse } from "@/schemas/api";
import {
  SendMessageInputSchema,
  type ConversationDetail,
  type ConversationSummary,
} from "@/schemas/ai";
import * as aiService from "@/services/ai";

/**
 * Server Actions for the AI assistant. Validate input with Zod, then delegate to
 * the services layer. Results are serializable for UI consumption.
 */

const conversationIdSchema = z.uuid();

function firstError(error: z.ZodError): string {
  return error.issues[0]?.message ?? "Invalid input";
}

export async function listConversationsAction(): Promise<ApiResponse<ConversationSummary[]>> {
  return aiService.listConversations();
}

export async function getConversationAction(
  conversationId: string,
): Promise<ApiResponse<ConversationDetail>> {
  const parsed = conversationIdSchema.safeParse(conversationId);
  if (!parsed.success) {
    return { success: false, error: "Invalid conversation id" };
  }
  return aiService.getConversation(parsed.data);
}

export async function startConversationAction(
  input: unknown,
): Promise<ApiResponse<ConversationDetail>> {
  const parsed = SendMessageInputSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: firstError(parsed.error) };
  }
  return aiService.startConversation(parsed.data);
}

export async function sendMessageAction(
  conversationId: string,
  input: unknown,
): Promise<ApiResponse<ConversationDetail>> {
  const id = conversationIdSchema.safeParse(conversationId);
  if (!id.success) {
    return { success: false, error: "Invalid conversation id" };
  }
  const parsed = SendMessageInputSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: firstError(parsed.error) };
  }
  return aiService.sendMessage(id.data, parsed.data);
}
