"use client";

import { useState } from "react";
import { watchLesson } from "@/client/actions/learning";
import type { LessonSummary } from "@/schemas/skill-tree";

/**
 * UI state for the lesson player (issue #22): tracks watched status and marks a
 * lesson watched once. Calls `onWatched` after a successful mark so the parent
 * can update node progress.
 */
export function useLessonPlayer(
  lesson: LessonSummary,
  initialWatched: boolean,
  onWatched?: () => void,
) {
  const [watched, setWatched] = useState(initialWatched);
  const [marking, setMarking] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function markWatched() {
    if (watched || marking) {
      return;
    }
    setMarking(true);
    setError(null);
    const res = await watchLesson(lesson.id);
    setMarking(false);
    if (res.success) {
      setWatched(true);
      onWatched?.();
    } else {
      setError(res.error);
    }
  }

  return { watched, marking, error, markWatched };
}
