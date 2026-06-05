'use server';

import { cookies } from 'next/headers';
import * as authService from '@/services/auth.service';
import {
  ConfirmResetSchema,
  LoginSchema,
  RequestResetSchema,
  SignupSchema,
} from '@/schemas/auth';
import type { ApiResponse } from '@/schemas/api';
import type { AuthResponse, UserSummary } from '@/schemas/auth';

const ACCESS_COOKIE = 'accessToken';
const REFRESH_COOKIE = 'refreshToken';

/**
 * Server Actions for the Auth feature. These validate input, call the auth
 * service, and own the session-cookie side effects (httpOnly so the token is
 * never exposed to client JS; `http()` reads the same `accessToken` cookie).
 */

async function persistSession(auth: AuthResponse): Promise<void> {
  const store = await cookies();
  const secure = process.env.NODE_ENV === 'production';
  store.set(ACCESS_COOKIE, auth.accessToken, {
    httpOnly: true,
    secure,
    sameSite: 'lax',
    path: '/',
    maxAge: auth.expiresIn,
  });
  store.set(REFRESH_COOKIE, auth.refreshToken, {
    httpOnly: true,
    secure,
    sameSite: 'lax',
    path: '/',
    // Refresh tokens are longer-lived; 30 days is a sensible client ceiling.
    maxAge: 60 * 60 * 24 * 30,
  });
}

async function clearSession(): Promise<void> {
  const store = await cookies();
  store.delete(ACCESS_COOKIE);
  store.delete(REFRESH_COOKIE);
}

export async function signupAction(input: unknown): Promise<ApiResponse<UserSummary>> {
  const parsed = SignupSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  const res = await authService.signup(parsed.data);
  if (!res.success) return res;
  await persistSession(res.data);
  return { success: true, data: res.data.user };
}

export async function loginAction(input: unknown): Promise<ApiResponse<UserSummary>> {
  const parsed = LoginSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  const res = await authService.login(parsed.data);
  if (!res.success) return res;
  await persistSession(res.data);
  return { success: true, data: res.data.user };
}

export async function logoutAction(): Promise<ApiResponse<void>> {
  const store = await cookies();
  const refreshToken = store.get(REFRESH_COOKIE)?.value;
  if (refreshToken) {
    // Best-effort server-side revocation; clear the session regardless.
    await authService.logout(refreshToken);
  }
  await clearSession();
  return { success: true, data: undefined };
}

export async function getCurrentUserAction(): Promise<ApiResponse<UserSummary>> {
  return authService.getCurrentUser();
}

export async function requestPasswordResetAction(
  input: unknown,
): Promise<ApiResponse<{ message: string }>> {
  const parsed = RequestResetSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return authService.requestPasswordReset(parsed.data);
}

export async function confirmPasswordResetAction(
  input: unknown,
): Promise<ApiResponse<{ message: string }>> {
  const parsed = ConfirmResetSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return authService.confirmPasswordReset(parsed.data);
}
