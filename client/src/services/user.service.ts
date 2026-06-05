import * as usersApi from '@/external/users';
import type { ApiResponse } from '@/schemas/api';
import type {
  AvatarResponse,
  Preferences,
  Profile,
  PublicProfile,
  UpdateProfileInput,
  XpLevel,
} from '@/schemas/user';

/**
 * Application logic for the User/Profile use-cases. Orchestrates the user
 * external layer; no UI concerns and no request parsing here.
 */

export async function getOwnProfile(): Promise<ApiResponse<Profile>> {
  return usersApi.getOwnProfile();
}

export async function updateOwnProfile(input: UpdateProfileInput): Promise<ApiResponse<Profile>> {
  return usersApi.updateOwnProfile(input);
}

export async function deleteOwnProfile(): Promise<ApiResponse<void>> {
  return usersApi.deleteOwnProfile();
}

export async function getPreferences(): Promise<ApiResponse<Preferences>> {
  return usersApi.getPreferences();
}

export async function updatePreferences(input: Preferences): Promise<ApiResponse<Preferences>> {
  return usersApi.updatePreferences(input);
}

export async function getPublicProfile(userId: string): Promise<ApiResponse<PublicProfile>> {
  return usersApi.getPublicProfile(userId);
}

export async function getXpLevel(userId: string): Promise<ApiResponse<XpLevel>> {
  return usersApi.getXpLevel(userId);
}

export async function uploadAvatar(file: File): Promise<ApiResponse<AvatarResponse>> {
  return usersApi.uploadAvatar(file);
}
