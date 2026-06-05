import { http } from '@/external/http';
import type { ApiResponse } from '@/schemas/api';
import type { FollowCounts, FollowStatus, SocialUserPage } from '@/schemas/social';

/**
 * Backend integration for the Social Service (/api/social). Each function maps
 * 1:1 to a SocialController endpoint: request in, DTO out, no business rules.
 */

export function follow(userId: string): Promise<ApiResponse<void>> {
  return http({ method: 'POST', path: `/api/social/follow/${userId}` });
}

export function unfollow(userId: string): Promise<ApiResponse<void>> {
  return http({ method: 'DELETE', path: `/api/social/follow/${userId}` });
}

export function isFollowing(userId: string): Promise<ApiResponse<FollowStatus>> {
  return http({ method: 'GET', path: `/api/social/follow/${userId}/status` });
}

export function getCounts(userId: string): Promise<ApiResponse<FollowCounts>> {
  return http({ method: 'GET', path: `/api/social/users/${userId}/counts` });
}

export function getFollowers(userId: string, page = 0, size = 20): Promise<ApiResponse<SocialUserPage>> {
  return http({ method: 'GET', path: `/api/social/users/${userId}/followers?page=${page}&size=${size}` });
}

export function getFollowing(userId: string, page = 0, size = 20): Promise<ApiResponse<SocialUserPage>> {
  return http({ method: 'GET', path: `/api/social/users/${userId}/following?page=${page}&size=${size}` });
}
