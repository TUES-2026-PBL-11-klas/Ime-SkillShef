import {
  completeNode as completeNodeApi,
  fetchSkillTree,
  unlockNode as unlockNodeApi,
} from "@/external/skills";
import type { ApiResponse } from "@/schemas/api";
import { sortByTier } from "@/domain/skill-tree";
import type { Progress, SkillTreeResponse } from "@/schemas/skill-tree";

/**
 * Skill tree use-cases (issue #22). Orchestrates the external API and applies
 * domain ordering so the UI receives nodes in a stable, tier-ascending order.
 */

export async function getSkillTree(): Promise<ApiResponse<SkillTreeResponse>> {
  const result = await fetchSkillTree();
  if (!result.success) {
    return result;
  }

  const domains = result.data.domains.map((domain) => ({
    ...domain,
    nodes: sortByTier(domain.nodes),
  }));
  return { success: true, data: { domains } };
}

export async function unlockSkillNode(nodeId: string): Promise<ApiResponse<Progress>> {
  return unlockNodeApi(nodeId);
}

export async function completeSkillNode(nodeId: string): Promise<ApiResponse<Progress>> {
  return completeNodeApi(nodeId);
}
