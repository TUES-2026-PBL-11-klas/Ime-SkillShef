import * as socialApi from '@/external/social';
import type { ApiResponse } from '@/schemas/api';
import type { FollowCounts, FollowStatus, SocialUserPage } from '@/schemas/social';

/**
 * Application logic for the Social/Follows use-cases. Thin orchestration over
 * the social external layer.
 */

export async function follow(userId: string): Promise<ApiResponse<void>> {
  return socialApi.follow(userId);
}

export async function unfollow(userId: string): Promise<ApiResponse<void>> {
  return socialApi.unfollow(userId);
}

export async function isFollowing(userId: string): Promise<ApiResponse<FollowStatus>> {
  return socialApi.isFollowing(userId);
}

export async function getCounts(userId: string): Promise<ApiResponse<FollowCounts>> {
  return socialApi.getCounts(userId);
}

export async function getFollowers(
  userId: string,
  page: number,
  size: number,
): Promise<ApiResponse<SocialUserPage>> {
  return socialApi.getFollowers(userId, page, size);
}

export async function getFollowing(
  userId: string,
  page: number,
  size: number,
): Promise<ApiResponse<SocialUserPage>> {
  return socialApi.getFollowing(userId, page, size);
}
