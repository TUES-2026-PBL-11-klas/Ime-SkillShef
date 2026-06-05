import {
  followAction,
  getCountsAction,
  getFollowersAction,
  getFollowingAction,
  isFollowingAction,
  unfollowAction,
} from '@/actions/social.actions';
import type { ApiResponse } from '@/schemas/api';
import type { FollowCounts, FollowStatus, SocialUserPage } from '@/schemas/social';

/**
 * Client Actions for Social/Follows: typed wrappers over the Server Actions.
 */

export function follow(userId: string): Promise<ApiResponse<void>> {
  return followAction(userId);
}

export function unfollow(userId: string): Promise<ApiResponse<void>> {
  return unfollowAction(userId);
}

export function isFollowing(userId: string): Promise<ApiResponse<FollowStatus>> {
  return isFollowingAction(userId);
}

export function getCounts(userId: string): Promise<ApiResponse<FollowCounts>> {
  return getCountsAction(userId);
}

export function getFollowers(userId: string, page = 0, size = 20): Promise<ApiResponse<SocialUserPage>> {
  return getFollowersAction(userId, page, size);
}

export function getFollowing(userId: string, page = 0, size = 20): Promise<ApiResponse<SocialUserPage>> {
  return getFollowingAction(userId, page, size);
}
