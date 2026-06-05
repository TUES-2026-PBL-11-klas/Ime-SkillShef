import { getFollowingFeedAction, getTrendingFeedAction } from '@/actions/feed.actions';
import type { ApiResponse } from '@/schemas/api';
import type { PostPage } from '@/schemas/community';

export function getFollowingFeed(page: number, size: number): Promise<ApiResponse<PostPage>> {
  return getFollowingFeedAction(page, size);
}

export function getTrendingFeed(page: number, size: number): Promise<ApiResponse<PostPage>> {
  return getTrendingFeedAction(page, size);
}
