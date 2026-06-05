import {
  completeNodeAction,
  getSkillTreeAction,
  unlockNodeAction,
} from "@/actions/skill-tree";

/**
 * Client-side wrappers over the skill tree Server Actions (issue #22). Thin and
 * stable so hooks in `src/client/state` have a clean API to call.
 */

export function getSkillTree() {
  return getSkillTreeAction();
}

export function unlockNode(nodeId: string) {
  return unlockNodeAction(nodeId);
}

export function completeNode(nodeId: string) {
  return completeNodeAction(nodeId);
}
