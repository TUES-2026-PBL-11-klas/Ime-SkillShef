'use client';

import { useState, useMemo, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import {
  DIAGNOSTIC_QUESTIONS,
  isDiagnosticComplete,
  levelFromScore,
  scoreDiagnostic,
  type ExperienceLevel,
} from '@/domain/onboarding';

const STORAGE_KEY = 'skillchef.onboarding';

/**
 * Drives the onboarding diagnostic: collects answers, derives a recommended
 * experience level via the pure `domain/onboarding` rules, and persists the
 * result so we don't re-prompt returning users (MVP keeps this client-side
 * until a backend onboarding endpoint exists).
 */
export function useOnboarding() {
  const router = useRouter();
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [step, setStep] = useState(0);

  const answer = useCallback((questionId: string, value: string) => {
    setAnswers((prev) => ({ ...prev, [questionId]: value }));
  }, []);

  const total = DIAGNOSTIC_QUESTIONS.length;
  const complete = isDiagnosticComplete(answers);
  const recommendedLevel: ExperienceLevel = useMemo(
    () => levelFromScore(scoreDiagnostic(answers)),
    [answers],
  );

  const next = useCallback(() => setStep((s) => Math.min(s + 1, total)), [total]);
  const back = useCallback(() => setStep((s) => Math.max(s - 1, 0)), []);

  const finish = useCallback(() => {
    try {
      window.localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ level: recommendedLevel, completedAt: new Date().toISOString() }),
      );
    } catch {
      // localStorage may be unavailable (private mode); not fatal for the flow.
    }
    router.push('/skills');
    router.refresh();
  }, [recommendedLevel, router]);

  return {
    questions: DIAGNOSTIC_QUESTIONS,
    answers,
    step,
    total,
    complete,
    recommendedLevel,
    answer,
    next,
    back,
    finish,
  };
}
