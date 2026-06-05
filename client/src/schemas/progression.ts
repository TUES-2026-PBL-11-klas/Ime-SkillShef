import z from "zod";

/**
 * Zod schemas for the Progression API (issue #22 frontend), mirroring the
 * backend DTOs exposed under `/api/progression`.
 */

export const LevelProgressResponseSchema = z.object({
  userId: z.string(),
  globalXp: z.number(),
  level: z.number(),
  xpIntoLevel: z.number(),
  xpForCurrentLevel: z.number(),
  xpForNextLevel: z.number(),
  xpToNextLevel: z.number(),
  progressPercent: z.number(),
});
export type LevelProgressResponse = z.infer<typeof LevelProgressResponseSchema>;

export const LeaderboardEntrySchema = z.object({
  rank: z.number(),
  userId: z.string(),
  username: z.string(),
  avatarUrl: z.string().nullable(),
  globalXp: z.number(),
  level: z.number(),
});
export type LeaderboardEntry = z.infer<typeof LeaderboardEntrySchema>;

export const LeaderboardResponseSchema = z.object({
  entries: z.array(LeaderboardEntrySchema),
});
export type LeaderboardResponse = z.infer<typeof LeaderboardResponseSchema>;

export const RankResponseSchema = z.object({
  rank: z.number(),
});
export type RankResponse = z.infer<typeof RankResponseSchema>;

export const XpTransactionSchema = z.object({
  id: z.string(),
  amount: z.number(),
  reason: z.string(),
  sourceType: z.string().nullable(),
  sourceId: z.string().nullable(),
  createdAt: z.string().nullable(),
});
export type XpTransaction = z.infer<typeof XpTransactionSchema>;

export const XpHistoryResponseSchema = z.object({
  transactions: z.array(XpTransactionSchema),
});
export type XpHistoryResponse = z.infer<typeof XpHistoryResponseSchema>;
