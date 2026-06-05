import {
  fetchLeaderboard,
  fetchMyProgression,
  fetchMyRank,
  fetchXpHistory,
} from "@/external/progression";
import type { ApiResponse } from "@/schemas/api";
import type {
  LeaderboardResponse,
  LevelProgressResponse,
  RankResponse,
  XpHistoryResponse,
} from "@/schemas/progression";

/**
 * Progression use-cases (issue #22): level progress, rank, XP history and the
 * global leaderboard. Thin orchestration over the external layer.
 */

export async function getMyProgression(): Promise<ApiResponse<LevelProgressResponse>> {
  return fetchMyProgression();
}

export async function getMyRank(): Promise<ApiResponse<RankResponse>> {
  return fetchMyRank();
}

export async function getXpHistory(
  page: number,
  size: number,
): Promise<ApiResponse<XpHistoryResponse>> {
  return fetchXpHistory(page, size);
}

export async function getLeaderboard(limit: number): Promise<ApiResponse<LeaderboardResponse>> {
  return fetchLeaderboard(limit);
}
