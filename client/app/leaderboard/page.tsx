import Link from "next/link";
import { Leaderboard } from "@/components/progression/leaderboard";
import { getLeaderboard, getMyRank } from "@/services/progression";
import type { LeaderboardEntry } from "@/schemas/progression";

/**
 * Leaderboard page (issue #22). Server component listing the top chefs by XP
 * and the current user's rank.
 */
export const dynamic = "force-dynamic";

export default async function LeaderboardPage() {
  const [leaderboardRes, rankRes] = await Promise.all([getLeaderboard(50), getMyRank()]);

  const entries: LeaderboardEntry[] = leaderboardRes.success ? leaderboardRes.data.entries : [];
  const rank: number | null = rankRes.success ? rankRes.data.rank : null;

  return (
    <div className="mx-auto max-w-2xl px-4 py-8">
      <header className="mb-6 flex items-center justify-between gap-4">
        <h1 className="text-2xl font-bold">Leaderboard</h1>
        <Link href="/skills" className="text-sm font-medium text-primary hover:underline">
          &larr; Back to skills
        </Link>
      </header>

      {rank != null ? (
        <p className="mb-4 text-sm text-muted-foreground">
          Your rank: <span className="font-semibold text-foreground">#{rank}</span>
        </p>
      ) : null}

      <Leaderboard entries={entries} title="Top chefs" />
    </div>
  );
}
