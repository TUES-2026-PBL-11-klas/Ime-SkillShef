"use server";

import z from "zod";
import type { ApiResponse } from "@/schemas/api";
import type {
  LeaderboardResponse,
  LevelProgressResponse,
  RankResponse,
  XpHistoryResponse,
} from "@/schemas/progression";
import {
  getLeaderboard,
  getMyProgression,
  getMyRank,
  getXpHistory,
} from "@/services/progression";

/**
 * Server Actions for progression (issue #22). Validate input with Zod, then
 * delegate to the services layer.
 */

const paginationSchema = z.object({
  page: z.number().int().min(0).default(0),
  size: z.number().int().min(1).max(100).default(20),
});

const limitSchema = z.number().int().min(1).max(100).default(20);

export async function getProgressionAction(): Promise<ApiResponse<LevelProgressResponse>> {
  return getMyProgression();
}

export async function getRankAction(): Promise<ApiResponse<RankResponse>> {
  return getMyRank();
}

export async function getXpHistoryAction(
  page = 0,
  size = 20,
): Promise<ApiResponse<XpHistoryResponse>> {
  const parsed = paginationSchema.safeParse({ page, size });
  if (!parsed.success) {
    return { success: false, error: "Invalid pagination" };
  }
  return getXpHistory(parsed.data.page, parsed.data.size);
}

export async function getLeaderboardAction(limit = 20): Promise<ApiResponse<LeaderboardResponse>> {
  const parsed = limitSchema.safeParse(limit);
  if (!parsed.success) {
    return { success: false, error: "Invalid limit" };
  }
  return getLeaderboard(parsed.data);
}
