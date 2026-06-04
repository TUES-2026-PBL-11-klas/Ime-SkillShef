'use server';

import * as feedService from '@/services/feed.service';
import type { ApiResponse } from '@/schemas/api';
import type { PostPage } from '@/schemas/community';

export async function getFollowingFeedAction(page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return feedService.getFollowingFeed(page, size);
}

export async function getTrendingFeedAction(page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return feedService.getTrendingFeed(page, size);
}
