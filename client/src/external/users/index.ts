import { cookies } from 'next/headers';
import { http } from '@/external/http';
import { ErrorResponseSchema, type ApiResponse } from '@/schemas/api';
import type {
  AvatarResponse,
  Preferences,
  Profile,
  PublicProfile,
  UpdateProfileInput,
  XpLevel,
} from '@/schemas/user';

/**
 * Backend integration for the User Service (/api/users). Each function maps
 * 1:1 to a UserController endpoint: request in, DTO out, no business rules.
 */

export function getOwnProfile(): Promise<ApiResponse<Profile>> {
  return http({ method: 'GET', path: '/api/users/me' });
}

export function updateOwnProfile(input: UpdateProfileInput): Promise<ApiResponse<Profile>> {
  return http({ method: 'PUT', path: '/api/users/me', options: { body: input } });
}

export function deleteOwnProfile(): Promise<ApiResponse<void>> {
  return http({ method: 'DELETE', path: '/api/users/me' });
}

export function getPreferences(): Promise<ApiResponse<Preferences>> {
  return http({ method: 'GET', path: '/api/users/me/preferences' });
}

export function updatePreferences(input: Preferences): Promise<ApiResponse<Preferences>> {
  return http({ method: 'PUT', path: '/api/users/me/preferences', options: { body: input } });
}

export function getPublicProfile(userId: string): Promise<ApiResponse<PublicProfile>> {
  return http({ method: 'GET', path: `/api/users/${userId}` });
}

export function getXpLevel(userId: string): Promise<ApiResponse<XpLevel>> {
  return http({ method: 'GET', path: `/api/users/${userId}/xp` });
}

/**
 * Avatar upload uses multipart/form-data, which the shared JSON `http()`
 * abstraction does not handle, so this isolates that one transport detail here
 * (the bearer token is still attached from the same cookie http() reads).
 */
export async function uploadAvatar(file: File): Promise<ApiResponse<AvatarResponse>> {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

  const headers: Record<string, string> = {};
  try {
    const token = (await cookies()).get('accessToken')?.value;
    if (token) headers['Authorization'] = `Bearer ${token}`;
  } catch {
    // cookies() outside a request context — safe to ignore.
  }

  const form = new FormData();
  form.append('file', file);

  try {
    const response = await fetch(`${baseUrl}/api/users/me/avatar`, {
      method: 'POST',
      headers,
      body: form,
    });

    let json: unknown;
    try {
      json = await response.json();
    } catch {
      return { success: false, error: 'Invalid response format' };
    }

    if (response.ok) return { success: true, data: json as AvatarResponse };

    const parsedError = ErrorResponseSchema.safeParse(json);
    if (parsedError.success) return { success: false, error: parsedError.data.error };
    return { success: false, error: 'An unexpected error occurred' };
  } catch (error) {
    return { success: false, error: error instanceof Error ? error.message : 'Network error' };
  }
}
