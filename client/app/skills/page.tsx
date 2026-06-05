import { SkillTreeExperience } from "@/components/skill-tree/skill-tree-experience";
import { getSkillTree } from "@/services/skill-tree";
import { getLeaderboard, getMyProgression, getMyRank } from "@/services/progression";
import type { LeaderboardEntry, LevelProgressResponse } from "@/schemas/progression";
import type { SkillTreeResponse } from "@/schemas/skill-tree";

/**
 * Skill Tree page (issue #22). Server component that loads the tree, the user's
 * progression/rank and a compact leaderboard, then hands off to the interactive
 * client experience.
 */
export const dynamic = "force-dynamic";

export default async function SkillsPage() {
  const [treeRes, progressionRes, rankRes, leaderboardRes] = await Promise.all([
    getSkillTree(),
    getMyProgression(),
    getMyRank(),
    getLeaderboard(10),
  ]);

  const tree: SkillTreeResponse = treeRes.success ? treeRes.data : { domains: [] };
  const progression: LevelProgressResponse | null = progressionRes.success
    ? progressionRes.data
    : null;
  const rank: number | null = rankRes.success ? rankRes.data.rank : null;
  const leaderboard: LeaderboardEntry[] = leaderboardRes.success ? leaderboardRes.data.entries : [];

  return (
    <SkillTreeExperience
      initialTree={tree}
      initialProgression={progression}
      initialRank={rank}
      initialLeaderboard={leaderboard}
    />
  );
}
