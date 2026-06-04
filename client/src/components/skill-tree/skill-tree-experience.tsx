"use client";

import Link from "next/link";
import { useSkillTree } from "@/client/state/use-skill-tree";
import { useProgression } from "@/client/state/use-progression";
import { DomainSection } from "@/components/skill-tree/domain-section";
import { NodeDetailPanel } from "@/components/skill-tree/node-detail-panel";
import { Leaderboard } from "@/components/progression/leaderboard";
import { XpWidget } from "@/components/progression/xp-widget";
import type { LeaderboardEntry, LevelProgressResponse } from "@/schemas/progression";
import type { QuizResult, SkillNode, SkillTreeResponse } from "@/schemas/skill-tree";

/**
 * Top-level client orchestrator for the Skill Tree page (issue #22). Composes
 * the tree, XP/level widgets, leaderboard and the node detail drawer, wiring
 * unlock/complete/quiz interactions to the progression widgets.
 */
export function SkillTreeExperience({
  initialTree,
  initialProgression,
  initialRank,
  initialLeaderboard,
}: {
  initialTree: SkillTreeResponse;
  initialProgression: LevelProgressResponse | null;
  initialRank: number | null;
  initialLeaderboard: LeaderboardEntry[];
}) {
  const skillTree = useSkillTree(initialTree);
  const progression = useProgression(initialProgression);

  const selected = skillTree.selectedNode;
  const hasDomains = skillTree.tree.domains.length > 0;

  function handleQuizPassed(nodeId: string) {
    return (result: QuizResult) => {
      skillTree.setNodeProgressPercent(nodeId, result.nodeProgressPercent);
      void progression.refresh();
    };
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <header className="mb-6 flex items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Skill Tree</h1>
          <p className="text-sm text-muted-foreground">Master cooking skills tier by tier.</p>
        </div>
        <Link href="/leaderboard" className="text-sm font-medium text-primary hover:underline">
          View leaderboard &rarr;
        </Link>
      </header>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-8">
          {skillTree.error ? (
            <p className="rounded-md bg-destructive/10 px-4 py-2 text-sm text-destructive">
              {skillTree.error}
            </p>
          ) : null}

          {hasDomains ? (
            skillTree.tree.domains.map((domain) => (
              <DomainSection
                key={domain.id}
                domain={domain}
                pendingNodeId={skillTree.pendingNodeId}
                onOpen={(node: SkillNode) => skillTree.selectNode(node.id)}
                onUnlock={(node: SkillNode) => void skillTree.unlock(node.id)}
              />
            ))
          ) : (
            <p className="rounded-md border border-dashed border-border p-8 text-center text-sm text-muted-foreground">
              No skills available yet. Sign in to start your culinary journey.
            </p>
          )}
        </div>

        <aside className="space-y-6">
          <XpWidget progression={progression.progression} rank={initialRank} />
          <Leaderboard
            entries={initialLeaderboard}
            title="Top chefs"
            currentUserId={progression.progression?.userId ?? null}
          />
        </aside>
      </div>

      {selected ? (
        <div className="fixed inset-0 z-50 flex justify-end">
          <button
            type="button"
            className="absolute inset-0 bg-black/40"
            aria-label="Close panel"
            onClick={() => skillTree.selectNode(null)}
          />
          <div className="relative h-full w-full max-w-lg animate-slide-in">
            <NodeDetailPanel
              node={selected}
              pending={skillTree.pendingNodeId === selected.id}
              onClose={() => skillTree.selectNode(null)}
              onUnlock={() => void skillTree.unlock(selected.id)}
              onComplete={() => void skillTree.complete(selected.id)}
              onQuizPassed={handleQuizPassed(selected.id)}
            />
          </div>
        </div>
      ) : null}
    </div>
  );
}
