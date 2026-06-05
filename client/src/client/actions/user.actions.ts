import {
  deleteOwnProfileAction,
  getOwnProfileAction,
  getPreferencesAction,
  getPublicProfileAction,
  getXpLevelAction,
  updateOwnProfileAction,
  updatePreferencesAction,
  uploadAvatarAction,
} from '@/actions/user.actions';
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
 * Client Actions for User/Profile: typed wrappers over the Server Actions.
 */

export function getOwnProfile(): Promise<ApiResponse<Profile>> {
  return getOwnProfileAction();
}

export function updateOwnProfile(input: UpdateProfileInput): Promise<ApiResponse<Profile>> {
  return updateOwnProfileAction(input);
}

export function deleteOwnProfile(): Promise<ApiResponse<void>> {
  return deleteOwnProfileAction();
}

export function getPreferences(): Promise<ApiResponse<Preferences>> {
  return getPreferencesAction();
}

export function updatePreferences(input: Preferences): Promise<ApiResponse<Preferences>> {
  return updatePreferencesAction(input);
}

export function getPublicProfile(userId: string): Promise<ApiResponse<PublicProfile>> {
  return getPublicProfileAction(userId);
}

export function getXpLevel(userId: string): Promise<ApiResponse<XpLevel>> {
  return getXpLevelAction(userId);
}

export function uploadAvatar(formData: FormData): Promise<ApiResponse<AvatarResponse>> {
  return uploadAvatarAction(formData);
}
