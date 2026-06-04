"use client";

import { useCallback, useMemo, useState } from "react";
import { completeNode, unlockNode } from "@/client/actions/skill-tree";
import type { Progress, SkillNode, SkillTreeResponse } from "@/schemas/skill-tree";

/**
 * UI state for the skill tree (issue #22): holds the tree, the currently
 * selected node, and exposes unlock/complete handlers that optimistically
 * update local node progress from the server response.
 */
export function useSkillTree(initialTree: SkillTreeResponse) {
  const [tree, setTree] = useState<SkillTreeResponse>(initialTree);
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [pendingNodeId, setPendingNodeId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const selectedNode = useMemo<SkillNode | null>(() => {
    if (!selectedNodeId) {
      return null;
    }
    for (const domain of tree.domains) {
      const found = domain.nodes.find((node) => node.id === selectedNodeId);
      if (found) {
        return found;
      }
    }
    return null;
  }, [tree, selectedNodeId]);

  const applyProgress = useCallback((nodeId: string, progress: Progress) => {
    setTree((prev) => ({
      domains: prev.domains.map((domain) => ({
        ...domain,
        nodes: domain.nodes.map((node) =>
          node.id === nodeId ? { ...node, progress } : node,
        ),
      })),
    }));
  }, []);

  const setNodeProgressPercent = useCallback((nodeId: string, percent: number) => {
    setTree((prev) => ({
      domains: prev.domains.map((domain) => ({
        ...domain,
        nodes: domain.nodes.map((node) => {
          if (node.id !== nodeId) {
            return node;
          }
          const progress: Progress = node.progress ?? {
            progressPercent: 0,
            unlockedAt: null,
            completedAt: null,
          };
          return { ...node, progress: { ...progress, progressPercent: percent } };
        }),
      })),
    }));
  }, []);

  const unlock = useCallback(
    async (nodeId: string) => {
      setPendingNodeId(nodeId);
      setError(null);
      const res = await unlockNode(nodeId);
      setPendingNodeId(null);
      if (res.success) {
        applyProgress(nodeId, res.data);
      } else {
        setError(res.error);
      }
      return res;
    },
    [applyProgress],
  );

  const complete = useCallback(
    async (nodeId: string) => {
      setPendingNodeId(nodeId);
      setError(null);
      const res = await completeNode(nodeId);
      setPendingNodeId(null);
      if (res.success) {
        applyProgress(nodeId, res.data);
      } else {
        setError(res.error);
      }
      return res;
    },
    [applyProgress],
  );

  return {
    tree,
    selectedNode,
    selectNode: setSelectedNodeId,
    pendingNodeId,
    error,
    unlock,
    complete,
    setNodeProgressPercent,
  };
}
