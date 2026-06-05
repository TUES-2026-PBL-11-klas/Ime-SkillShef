'use server';

import * as userService from '@/services/user.service';
import { UpdateProfileSchema } from '@/schemas/user';
import type { ApiResponse } from '@/schemas/api';
import type {
  AvatarResponse,
  Preferences,
  Profile,
  PublicProfile,
  XpLevel,
} from '@/schemas/user';
import { PreferencesSchema } from '@/schemas/user';

/**
 * Server Actions for the User/Profile feature. Validate input with the
 * `src/schemas` Zod schemas, then delegate to the user service.
 */

export async function getOwnProfileAction(): Promise<ApiResponse<Profile>> {
  return userService.getOwnProfile();
}

export async function updateOwnProfileAction(input: unknown): Promise<ApiResponse<Profile>> {
  const parsed = UpdateProfileSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return userService.updateOwnProfile(parsed.data);
}

export async function deleteOwnProfileAction(): Promise<ApiResponse<void>> {
  return userService.deleteOwnProfile();
}

export async function getPreferencesAction(): Promise<ApiResponse<Preferences>> {
  return userService.getPreferences();
}

export async function updatePreferencesAction(input: unknown): Promise<ApiResponse<Preferences>> {
  const parsed = PreferencesSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return userService.updatePreferences(parsed.data);
}

export async function getPublicProfileAction(userId: string): Promise<ApiResponse<PublicProfile>> {
  return userService.getPublicProfile(userId);
}

export async function getXpLevelAction(userId: string): Promise<ApiResponse<XpLevel>> {
  return userService.getXpLevel(userId);
}

export async function uploadAvatarAction(formData: FormData): Promise<ApiResponse<AvatarResponse>> {
  const file = formData.get('file');
  if (!(file instanceof File) || file.size === 0) {
    return { success: false, error: 'Please choose an image to upload' };
  }
  return userService.uploadAvatar(file);
}
