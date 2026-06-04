import { http } from '@/external/http';
import type { ApiResponse } from '@/schemas/api';
import type { PostPage } from '@/schemas/community';

export function getFollowingFeed(page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return http({ method: 'GET', path: `/api/feed?sort=RECENT&page=${page}&size=${size}` });
}

export function getTrendingFeed(page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return http({ method: 'GET', path: `/api/feed?sort=TRENDING&page=${page}&size=${size}` });
}
