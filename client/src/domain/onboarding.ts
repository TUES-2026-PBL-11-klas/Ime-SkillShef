/**
 * Onboarding diagnostic — pure domain logic (no React, no I/O).
 *
 * The MVP onboarding flow asks the user a few self-assessment questions and
 * derives a recommended starting skill level. This encodes the scoring rules
 * the SkillShef spec describes ("начална диагностика ... определя
 * приблизителното ниво на знания и опит" — SkillShef.pdf), without any
 * framework or transport concerns.
 */

export type ExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export interface DiagnosticOption {
  /** Stable value persisted with the answer. */
  value: string;
  label: string;
  /** Points this option contributes to the experience score. */
  points: number;
}

export interface DiagnosticQuestion {
  id: string;
  prompt: string;
  options: DiagnosticOption[];
}

/** The fixed diagnostic questionnaire used during onboarding. */
export const DIAGNOSTIC_QUESTIONS: DiagnosticQuestion[] = [
  {
    id: 'frequency',
    prompt: 'How often do you cook from scratch?',
    options: [
      { value: 'never', label: 'Almost never', points: 0 },
      { value: 'sometimes', label: 'A few times a month', points: 1 },
      { value: 'weekly', label: 'Most weeks', points: 2 },
      { value: 'daily', label: 'Nearly every day', points: 3 },
    ],
  },
  {
    id: 'knife',
    prompt: 'How comfortable are you with knife work?',
    options: [
      { value: 'none', label: "I avoid it — it's intimidating", points: 0 },
      { value: 'basic', label: 'I can chop, slowly', points: 1 },
      { value: 'confident', label: 'Comfortable with most cuts', points: 2 },
      { value: 'expert', label: 'Fast, precise, second nature', points: 3 },
    ],
  },
  {
    id: 'techniques',
    prompt: 'Which of these have you done before?',
    options: [
      { value: 'follow', label: 'Followed a written recipe', points: 0 },
      { value: 'sauce', label: 'Made a pan sauce or emulsion', points: 1 },
      { value: 'bread', label: 'Baked bread or pastry from scratch', points: 2 },
      { value: 'improvise', label: 'Cook confidently without a recipe', points: 3 },
    ],
  },
  {
    id: 'goal',
    prompt: 'What best describes your goal?',
    options: [
      { value: 'survive', label: 'Learn the absolute basics', points: 0 },
      { value: 'expand', label: 'Build a reliable repertoire', points: 1 },
      { value: 'master', label: 'Master advanced techniques', points: 2 },
      { value: 'pro', label: 'Cook at a near-professional level', points: 3 },
    ],
  },
];

/** Highest score achievable across all questions. */
export const MAX_DIAGNOSTIC_SCORE = DIAGNOSTIC_QUESTIONS.reduce(
  (sum, q) => sum + Math.max(...q.options.map((o) => o.points)),
  0,
);

/** Sum the points for a set of answers (questionId -> option value). */
export function scoreDiagnostic(answers: Record<string, string>): number {
  return DIAGNOSTIC_QUESTIONS.reduce((sum, q) => {
    const chosen = q.options.find((o) => o.value === answers[q.id]);
    return sum + (chosen?.points ?? 0);
  }, 0);
}

/** Map a raw diagnostic score onto a recommended starting experience level. */
export function levelFromScore(score: number): ExperienceLevel {
  const ratio = MAX_DIAGNOSTIC_SCORE === 0 ? 0 : score / MAX_DIAGNOSTIC_SCORE;
  if (ratio >= 0.66) return 'ADVANCED';
  if (ratio >= 0.33) return 'INTERMEDIATE';
  return 'BEGINNER';
}

export const LEVEL_COPY: Record<ExperienceLevel, { title: string; description: string }> = {
  BEGINNER: {
    title: 'Beginner',
    description:
      "We'll start you at the foundations — knife skills, heat control, and core techniques — so every later skill has solid ground to build on.",
  },
  INTERMEDIATE: {
    title: 'Intermediate',
    description:
      "You've got the basics down. We'll skip ahead to sauces, proteins, and building real repertoire while filling any gaps along the way.",
  },
  ADVANCED: {
    title: 'Advanced',
    description:
      "You cook with confidence. We'll point you toward advanced techniques, pastry, and challenges that push your craft further.",
  },
};

/** True once the user has answered every diagnostic question. */
export function isDiagnosticComplete(answers: Record<string, string>): boolean {
  return DIAGNOSTIC_QUESTIONS.every((q) => Boolean(answers[q.id]));
}
