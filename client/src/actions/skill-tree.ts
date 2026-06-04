"use server";

import z from "zod";
import type { ApiResponse } from "@/schemas/api";
import type { Progress, SkillTreeResponse } from "@/schemas/skill-tree";
import {
  completeSkillNode,
  getSkillTree,
  unlockSkillNode,
} from "@/services/skill-tree";

/**
 * Server Actions for the skill tree (issue #22). Validate input with Zod, then
 * delegate to the services layer. Results are serializable for UI consumption.
 */

const nodeIdSchema = z.uuid();

export async function getSkillTreeAction(): Promise<ApiResponse<SkillTreeResponse>> {
  return getSkillTree();
}

export async function unlockNodeAction(nodeId: string): Promise<ApiResponse<Progress>> {
  const parsed = nodeIdSchema.safeParse(nodeId);
  if (!parsed.success) {
    return { success: false, error: "Invalid node id" };
  }
  return unlockSkillNode(parsed.data);
}

export async function completeNodeAction(nodeId: string): Promise<ApiResponse<Progress>> {
  const parsed = nodeIdSchema.safeParse(nodeId);
  if (!parsed.success) {
    return { success: false, error: "Invalid node id" };
  }
  return completeSkillNode(parsed.data);
}
