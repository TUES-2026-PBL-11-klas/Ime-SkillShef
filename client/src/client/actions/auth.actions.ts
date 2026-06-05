import {
  confirmPasswordResetAction,
  getCurrentUserAction,
  loginAction,
  logoutAction,
  requestPasswordResetAction,
  signupAction,
} from '@/actions/auth.actions';
import type { ApiResponse } from '@/schemas/api';
import type {
  ConfirmResetInput,
  LoginInput,
  RequestResetInput,
  SignupInput,
  UserSummary,
} from '@/schemas/auth';

/**
 * Client Actions for Auth: stable, typed wrappers that hooks call. They forward
 * input to the Server Actions in `src/actions` — no validation, no logic here.
 */

export function signup(input: SignupInput): Promise<ApiResponse<UserSummary>> {
  return signupAction(input);
}

export function login(input: LoginInput): Promise<ApiResponse<UserSummary>> {
  return loginAction(input);
}

export function logout(): Promise<ApiResponse<void>> {
  return logoutAction();
}

export function getCurrentUser(): Promise<ApiResponse<UserSummary>> {
  return getCurrentUserAction();
}

export function requestPasswordReset(
  input: RequestResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return requestPasswordResetAction(input);
}

export function confirmPasswordReset(
  input: ConfirmResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return confirmPasswordResetAction(input);
}
