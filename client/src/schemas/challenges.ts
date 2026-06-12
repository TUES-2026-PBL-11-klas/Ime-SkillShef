import { z } from "zod";

/**
 * Single source of truth for challenge/submission types/validation.
 * Mirrors the backend ChallengeDtos (`/api/challenges`).
 */

export const SubmissionSchema = z.object({
  id: z.string(),
  challengeId: z.string(),
  userId: z.string(),
  mediaUrl: z.string(),
  aiFeedback: z.string().nullable(),
  score: z.number().nullable(),
  createdAt: z.string(),
});
export type Submission = z.infer<typeof SubmissionSchema>;

export const ChallengeSchema = z.object({
  id: z.string(),
  title: z.string(),
  type: z.string(),
  xpReward: z.number(),
  startDate: z.string(),
  endDate: z.string(),
  active: z.boolean(),
  submissionCount: z.number(),
});
export type Challenge = z.infer<typeof ChallengeSchema>;
export const ChallengeListSchema = z.array(ChallengeSchema);

export const ChallengeDetailSchema = z.object({
  id: z.string(),
  title: z.string(),
  type: z.string(),
  xpReward: z.number(),
  startDate: z.string(),
  endDate: z.string(),
  active: z.boolean(),
  submissions: z.array(SubmissionSchema),
});
export type ChallengeDetail = z.infer<typeof ChallengeDetailSchema>;
