import z from 'zod';

/**
 * Validation schemas + inferred types for the Auth feature (issue: Auth & User System).
 * Single source of truth for signup/login/reset inputs and the auth response shape.
 * Mirrors the backend AuthDtos (server/.../auth/dto/AuthDtos.java).
 */

// --- Inputs ---

export const SignupSchema = z.object({
  username: z
    .string()
    .min(3, 'Username must be at least 3 characters')
    .max(30, 'Username must be at most 30 characters'),
  email: z.string().email('Enter a valid email').max(255),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .max(100, 'Password must be at most 100 characters'),
});

export const LoginSchema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(1, 'Password is required'),
});

export const RequestResetSchema = z.object({
  email: z.string().email('Enter a valid email'),
});

export const ConfirmResetSchema = z.object({
  token: z.string().min(1, 'Reset token is required'),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .max(100, 'Password must be at most 100 characters'),
});

// --- Responses ---

export const UserSummarySchema = z.object({
  id: z.string().uuid(),
  username: z.string(),
  email: z.string(),
  globalXp: z.number(),
  level: z.number(),
});

export const AuthResponseSchema = z.object({
  accessToken: z.string(),
  refreshToken: z.string(),
  tokenType: z.string(),
  expiresIn: z.number(),
  user: UserSummarySchema,
});

// --- Inferred types ---

export type SignupInput = z.infer<typeof SignupSchema>;
export type LoginInput = z.infer<typeof LoginSchema>;
export type RequestResetInput = z.infer<typeof RequestResetSchema>;
export type ConfirmResetInput = z.infer<typeof ConfirmResetSchema>;
export type UserSummary = z.infer<typeof UserSummarySchema>;
export type AuthResponse = z.infer<typeof AuthResponseSchema>;
