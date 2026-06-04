import type { SkillNode } from "@/schemas/skill-tree";

/**
 * Pure domain helpers for the skill tree (issue #22). No side effects, no
 * framework dependencies — just rules that model node state and progression.
 */

export type NodeStatus = "completed" | "in-progress" | "available" | "locked";

/**
 * Derives the display status of a node from its progress and unlockability.
 * - completed: has a completedAt timestamp
 * - in-progress: unlocked but not yet completed
 * - available: not unlocked yet but eligible to unlock now
 * - locked: cannot be unlocked yet (prerequisite tier incomplete)
 */
export function nodeStatus(node: SkillNode): NodeStatus {
  const progress = node.progress;
  if (progress?.completedAt) {
    return "completed";
  }
  if (progress?.unlockedAt) {
    return "in-progress";
  }
  return node.unlockable ? "available" : "locked";
}

export function isCompleted(node: SkillNode): boolean {
  return nodeStatus(node) === "completed";
}

export function isUnlocked(node: SkillNode): boolean {
  const status = nodeStatus(node);
  return status === "completed" || status === "in-progress";
}

export function canUnlock(node: SkillNode): boolean {
  return nodeStatus(node) === "available";
}

/** Sorts nodes by tier ascending, leaving equal tiers in their original order. */
export function sortByTier(nodes: SkillNode[]): SkillNode[] {
  return [...nodes].sort((a, b) => a.tier - b.tier);
}

/** Groups nodes by their tier, preserving ascending tier order. */
export function groupByTier(nodes: SkillNode[]): Array<{ tier: number; nodes: SkillNode[] }> {
  const byTier = new Map<number, SkillNode[]>();
  for (const node of sortByTier(nodes)) {
    const bucket = byTier.get(node.tier) ?? [];
    bucket.push(node);
    byTier.set(node.tier, bucket);
  }
  return Array.from(byTier.entries()).map(([tier, tierNodes]) => ({ tier, nodes: tierNodes }));
}

/** Whether the node has any learning content (lessons or quizzes) to open. */
export function hasContent(node: SkillNode): boolean {
  return node.lessons.length > 0 || node.quizzes.length > 0;
}

/** Formats a node duration (sum of its lessons) as a short `Xm` / `Xh Ym` label. */
export function formatDuration(totalSeconds: number): string {
  if (totalSeconds <= 0) {
    return "0m";
  }
  const minutes = Math.round(totalSeconds / 60);
  if (minutes < 60) {
    return `${minutes}m`;
  }
  const hours = Math.floor(minutes / 60);
  const remainder = minutes % 60;
  return remainder === 0 ? `${hours}h` : `${hours}h ${remainder}m`;
}
