import z from "zod";

/**
 * Zod schemas for the Skill Tree, Lesson and Quiz APIs (issue #22 frontend).
 * These mirror the backend DTOs exposed under `/api/skills`, `/api/lessons`
 * and `/api/quizzes`.
 */

// ----- Skill tree --------------------------------------------------------

export const ProgressSchema = z.object({
  progressPercent: z.number(),
  unlockedAt: z.string().nullable(),
  completedAt: z.string().nullable(),
});
export type Progress = z.infer<typeof ProgressSchema>;

export const LessonSummarySchema = z.object({
  id: z.string(),
  nodeId: z.string(),
  title: z.string(),
  description: z.string().nullable(),
  videoUrl: z.string(),
  durationSeconds: z.number(),
});
export type LessonSummary = z.infer<typeof LessonSummarySchema>;

export const QuizSummarySchema = z.object({
  id: z.string(),
  nodeId: z.string(),
  question: z.string(),
  options: z.array(z.string()),
});
export type QuizSummary = z.infer<typeof QuizSummarySchema>;

export const SkillNodeSchema = z.object({
  id: z.string(),
  domainId: z.string(),
  parentNodeId: z.string().nullable(),
  title: z.string(),
  description: z.string().nullable(),
  tier: z.number(),
  xpReward: z.number(),
  unlockable: z.boolean(),
  progress: ProgressSchema.nullable(),
  lessons: z.array(LessonSummarySchema),
  quizzes: z.array(QuizSummarySchema),
});
export type SkillNode = z.infer<typeof SkillNodeSchema>;

export const SkillDomainSchema = z.object({
  id: z.string(),
  name: z.string(),
  description: z.string().nullable(),
  nodes: z.array(SkillNodeSchema),
});
export type SkillDomain = z.infer<typeof SkillDomainSchema>;

export const SkillTreeResponseSchema = z.object({
  domains: z.array(SkillDomainSchema),
});
export type SkillTreeResponse = z.infer<typeof SkillTreeResponseSchema>;

// ----- Lessons -----------------------------------------------------------

export const LessonResponseSchema = z.object({
  id: z.string(),
  nodeId: z.string(),
  title: z.string(),
  description: z.string().nullable(),
  videoUrl: z.string(),
  durationSeconds: z.number(),
  watched: z.boolean(),
  watchedAt: z.string().nullable(),
});
export type LessonResponse = z.infer<typeof LessonResponseSchema>;

export const LessonPlaybackResponseSchema = z.object({
  lessonId: z.string(),
  videoUrl: z.string(),
  durationSeconds: z.number(),
});
export type LessonPlaybackResponse = z.infer<typeof LessonPlaybackResponseSchema>;

export const WatchResponseSchema = z.object({
  lessonId: z.string(),
  watched: z.boolean(),
  watchedAt: z.string().nullable(),
});
export type WatchResponse = z.infer<typeof WatchResponseSchema>;

// ----- Quizzes -----------------------------------------------------------

export const QuizPublicSchema = z.object({
  id: z.string(),
  nodeId: z.string(),
  question: z.string(),
  options: z.array(z.string()),
  passed: z.boolean(),
});
export type QuizPublic = z.infer<typeof QuizPublicSchema>;

export const QuizResultSchema = z.object({
  quizId: z.string(),
  correct: z.boolean(),
  firstPass: z.boolean(),
  awardedXp: z.number(),
  newGlobalXp: z.number(),
  newLevel: z.number(),
  nodeProgressPercent: z.number(),
});
export type QuizResult = z.infer<typeof QuizResultSchema>;

export const QuizSubmissionSchema = z.object({
  selectedAnswer: z.string().min(1, "Please select an answer"),
});
export type QuizSubmission = z.infer<typeof QuizSubmissionSchema>;
