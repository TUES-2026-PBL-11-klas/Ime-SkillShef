import { cookies } from "next/headers";
import { http } from "@/external/http";
import type { ApiResponse } from "@/schemas/api";
import type {
  AuthResponse,
  ConfirmResetInput,
  LoginInput,
  RequestResetInput,
  SignupInput,
  UserSummary,
} from "@/schemas/auth";

/**
 * Server-only helper that builds the Authorization header from the auth cookie
 * set by the auth flow (issue #21). Returns an empty object when no token is
 * present so unauthenticated calls still produce a clean 401 from the backend.
 *
 * Centralised here so every external call attaches the bearer token the same way.
 */
export async function getAuthHeaders(): Promise<Record<string, string>> {
  try {
    const store = await cookies();
    const token = store.get("accessToken")?.value;
    return token ? { Authorization: `Bearer ${token}` } : {};
  } catch {
    // cookies() throws outside a request scope; treat as unauthenticated.
    return {};
  }
}

// --- Auth endpoints (map 1:1 to AuthController, /api/auth) ---

export function signup(input: SignupInput): Promise<ApiResponse<AuthResponse>> {
  return http({ method: "POST", path: "/api/auth/signup", options: { body: input } });
}

export function login(input: LoginInput): Promise<ApiResponse<AuthResponse>> {
  return http({ method: "POST", path: "/api/auth/login", options: { body: input } });
}

export function refresh(refreshToken: string): Promise<ApiResponse<AuthResponse>> {
  return http({ method: "POST", path: "/api/auth/refresh", options: { body: { refreshToken } } });
}

export function logout(refreshToken: string): Promise<ApiResponse<{ message: string }>> {
  return http({ method: "POST", path: "/api/auth/logout", options: { body: { refreshToken } } });
}

export function getMe(): Promise<ApiResponse<UserSummary>> {
  return http({ method: "GET", path: "/api/auth/me" });
}

/**
 * Password reset flow. The backend reset endpoints are owned by Person 1's
 * Auth Service; these map to the conventional request/confirm pair so the UI
 * is ready as soon as those endpoints land.
 */
export function requestPasswordReset(
  input: RequestResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return http({
    method: "POST",
    path: "/api/auth/password-reset",
    options: { body: input },
  });
}

export function confirmPasswordReset(
  input: ConfirmResetInput,
): Promise<ApiResponse<{ message: string }>> {
  return http({
    method: "POST",
    path: "/api/auth/password-reset/confirm",
    options: { body: input },
  });
}
