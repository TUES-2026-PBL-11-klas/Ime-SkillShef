import * as authApi from '@/external/auth';
import type { ApiResponse } from '@/schemas/api';
import type {
  AuthResponse,
  ConfirmResetInput,
  LoginInput,
  RequestResetInput,
  SignupInput,
  UserSummary,
} from '@/schemas/auth';

/**
 * Application logic for the Auth use-cases. Orchestrates calls to the auth
 * external layer. Cookie/session side effects live in the Server Actions; this
 * layer stays transport- and UI-agnostic.
 */

export async function signup(input: SignupInput): Promise<ApiResponse<AuthResponse>> {
  return authApi.signup(input);
}

export async function login(input: LoginInput): Promise<ApiResponse<AuthResponse>> {
  return authApi.login(input);
}

export async function refresh(refreshToken: string): Promise<ApiResponse<AuthResponse>> {
  return authApi.refresh(refreshToken);
}

export async function logout(refreshToken: string): Promise<ApiResponse<{ message: string }>> {
  return authApi.logout(refreshToken);
}

export async function getCurrentUser(): Promise<ApiResponse<UserSummary>> {
  return authApi.getMe();
}

export async function requestPasswordReset(
  input: RequestResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return authApi.requestPasswordReset(input);
}

export async function confirmPasswordReset(
  input: ConfirmResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return authApi.confirmPasswordReset(input);
}
