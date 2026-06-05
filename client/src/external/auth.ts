import { cookies } from "next/headers";

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
    const token = store.get("access_token")?.value;
    return token ? { Authorization: `Bearer ${token}` } : {};
  } catch {
    // cookies() throws outside a request scope; treat as unauthenticated.
    return {};
  }
}
