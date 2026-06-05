'use server';

import * as socialService from '@/services/social.service';
import type { ApiResponse } from '@/schemas/api';
import type { FollowCounts, FollowStatus, SocialUserPage } from '@/schemas/social';

/**
 * Server Actions for the Social/Follows feature. Thin entrypoints over the
 * social service.
 */

export async function followAction(userId: string): Promise<ApiResponse<void>> {
  return socialService.follow(userId);
}

export async function unfollowAction(userId: string): Promise<ApiResponse<void>> {
  return socialService.unfollow(userId);
}

export async function isFollowingAction(userId: string): Promise<ApiResponse<FollowStatus>> {
  return socialService.isFollowing(userId);
}

export async function getCountsAction(userId: string): Promise<ApiResponse<FollowCounts>> {
  return socialService.getCounts(userId);
}

export async function getFollowersAction(
  userId: string,
  page = 0,
  size = 20,
): Promise<ApiResponse<SocialUserPage>> {
  return socialService.getFollowers(userId, page, size);
}

export async function getFollowingAction(
  userId: string,
  page = 0,
  size = 20,
): Promise<ApiResponse<SocialUserPage>> {
  return socialService.getFollowing(userId, page, size);
}
