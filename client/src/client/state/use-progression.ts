"use client";

import { useState } from "react";
import { getProgression } from "@/client/actions/progression";
import type { LevelProgressResponse } from "@/schemas/progression";

/**
 * UI state for the XP/level widgets (issue #22): holds the current progression
 * snapshot and can refresh it after XP-awarding actions (e.g. passing a quiz).
 */
export function useProgression(initial: LevelProgressResponse | null) {
  const [progression, setProgression] = useState<LevelProgressResponse | null>(initial);
  const [refreshing, setRefreshing] = useState(false);

  async function refresh() {
    setRefreshing(true);
    const res = await getProgression();
    setRefreshing(false);
    if (res.success) {
      setProgression(res.data);
    }
  }

  return { progression, refreshing, refresh };
}
