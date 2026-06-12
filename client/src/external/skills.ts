import { http } from "@/external/http";
import { getAuthHeaders } from "@/external/auth";
import {
  ProgressSchema,
  SkillTreeResponseSchema,
  type Progress,
  type SkillTreeResponse,
} from "@/schemas/skill-tree";

/**
 * Backend integration for the Skill Tree API (`/api/skills`). Each function
 * maps 1:1 to a backend endpoint.
 */

export async function fetchSkillTree() {
  return http<SkillTreeResponse>({
    method: "GET",
    path: "/api/skills/tree",
    options: { headers: await getAuthHeaders() },
    schema: SkillTreeResponseSchema,
  });
}

export async function unlockNode(nodeId: string) {
  return http<Progress>({
    method: "POST",
    path: `/api/skills/nodes/${nodeId}/unlock`,
    options: { headers: await getAuthHeaders() },
    schema: ProgressSchema,
  });
}

export async function completeNode(nodeId: string) {
  return http<Progress>({
    method: "POST",
    path: `/api/skills/nodes/${nodeId}/complete`,
    options: { headers: await getAuthHeaders() },
    schema: ProgressSchema,
  });
}
