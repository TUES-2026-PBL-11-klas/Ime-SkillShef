import * as feedApi from '@/external/feed';
import type { PostPage } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';

export async function getFollowingFeed(page: number, size: number): Promise<ApiResponse<PostPage>> {
  return feedApi.getFollowingFeed(page, size);
}

export async function getTrendingFeed(page: number, size: number): Promise<ApiResponse<PostPage>> {
  return feedApi.getTrendingFeed(page, size);
}
