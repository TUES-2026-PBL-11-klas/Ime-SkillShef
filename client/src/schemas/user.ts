import z from 'zod';

/**
 * Validation schemas + inferred types for the User/Profile feature.
 * Mirrors the backend UserDtos (server/.../user/UserDtos.java).
 */

export const PreferencesSchema = z.object({
  notifications: z.boolean(),
  theme: z.enum(['light', 'dark', 'system']),
  language: z.string().min(2).max(10),
});

export const ProfileSchema = z.object({
  id: z.string().uuid(),
  username: z.string(),
  email: z.string(),
  avatarUrl: z.string().nullable(),
  bio: z.string().nullable(),
  preferences: PreferencesSchema,
  globalXp: z.number(),
  level: z.number(),
  createdAt: z.string(),
});

export const PublicProfileSchema = z.object({
  id: z.string().uuid(),
  username: z.string(),
  avatarUrl: z.string().nullable(),
  bio: z.string().nullable(),
  globalXp: z.number(),
  level: z.number(),
  createdAt: z.string(),
});

export const XpLevelSchema = z.object({
  userId: z.string().uuid(),
  globalXp: z.number(),
  level: z.number(),
});

export const UpdateProfileSchema = z.object({
  username: z
    .string()
    .min(3, 'Username must be at least 3 characters')
    .max(30, 'Username must be at most 30 characters')
    .optional(),
  bio: z.string().max(2000, 'Bio must be at most 2000 characters').optional(),
  preferences: PreferencesSchema.optional(),
});

export const AvatarResponseSchema = z.object({ avatarUrl: z.string() });

// --- Inferred types ---

export type Preferences = z.infer<typeof PreferencesSchema>;
export type Profile = z.infer<typeof ProfileSchema>;
export type PublicProfile = z.infer<typeof PublicProfileSchema>;
export type XpLevel = z.infer<typeof XpLevelSchema>;
export type UpdateProfileInput = z.infer<typeof UpdateProfileSchema>;
export type AvatarResponse = z.infer<typeof AvatarResponseSchema>;
