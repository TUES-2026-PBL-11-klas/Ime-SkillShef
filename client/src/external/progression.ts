import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  LeaderboardResponseSchema,
  LevelProgressResponseSchema,
  RankResponseSchema,
  XpHistoryResponseSchema,
  type LeaderboardResponse,
  type LevelProgressResponse,
  type RankResponse,
  type XpHistoryResponse,
} from "@/schemas/progression";

/**
 * Backend integration for the Progression API (`/api/progression`). Each
 * function maps 1:1 to a backend endpoint.
 */

export async function fetchMyProgression() {
  return http<LevelProgressResponse>({
    method: "GET",
    path: "/api/progression/me",
    options: { headers: await getAuthHeaders() },
    schema: LevelProgressResponseSchema,
  });
}

export async function fetchMyRank() {
  return http<RankResponse>({
    method: "GET",
    path: "/api/progression/me/rank",
    options: { headers: await getAuthHeaders() },
    schema: RankResponseSchema,
  });
}

export async function fetchXpHistory(page: number, size: number) {
  return http<XpHistoryResponse>({
    method: "GET",
    path: `/api/progression/me/xp-history?page=${page}&size=${size}`,
    options: { headers: await getAuthHeaders() },
    schema: XpHistoryResponseSchema,
  });
}

export async function fetchLeaderboard(limit: number) {
  return http<LeaderboardResponse>({
    method: "GET",
    path: `/api/progression/leaderboard?limit=${limit}`,
    options: { headers: await getAuthHeaders() },
    schema: LeaderboardResponseSchema,
  });
}
