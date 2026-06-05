"use client";

import { useState } from "react";
import { submitQuiz } from "@/client/actions/learning";
import type { QuizResult } from "@/schemas/skill-tree";

/**
 * UI state for running a single quiz (issue #22): tracks the selected option,
 * submission status and result. Invokes `onPassed` the first time the quiz is
 * passed so the surrounding UI can refresh XP/progress.
 */
export function useQuizRunner(quizId: string, onPassed?: (result: QuizResult) => void) {
  const [selected, setSelected] = useState<string | null>(null);
  const [result, setResult] = useState<QuizResult | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function submit() {
    if (!selected) {
      setError("Please select an answer");
      return;
    }
    setSubmitting(true);
    setError(null);
    const res = await submitQuiz(quizId, selected);
    setSubmitting(false);
    if (res.success) {
      setResult(res.data);
      if (res.data.firstPass) {
        onPassed?.(res.data);
      }
    } else {
      setError(res.error);
    }
  }

  function reset() {
    setSelected(null);
    setResult(null);
    setError(null);
  }

  return { selected, setSelected, result, submitting, error, submit, reset };
}
