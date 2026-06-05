import z from 'zod';

/**
 * Validation schemas + inferred types for the Social/Follows feature.
 * Mirrors the backend SocialDtos (server/.../user/SocialDtos.java).
 */

export const SocialUserSchema = z.object({
  id: z.string().uuid(),
  username: z.string(),
  avatarUrl: z.string().nullable(),
  level: z.number(),
});

export const FollowStatusSchema = z.object({ following: z.boolean() });

export const FollowCountsSchema = z.object({
  userId: z.string().uuid(),
  followers: z.number(),
  following: z.number(),
});

export const SocialUserPageSchema = z.object({
  items: z.array(SocialUserSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});

// --- Inferred types ---

export type SocialUser = z.infer<typeof SocialUserSchema>;
export type FollowStatus = z.infer<typeof FollowStatusSchema>;
export type FollowCounts = z.infer<typeof FollowCountsSchema>;
export type SocialUserPage = z.infer<typeof SocialUserPageSchema>;
