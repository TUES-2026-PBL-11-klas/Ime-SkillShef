'use server';

import * as postService from '@/services/post.service';
import { CreatePostSchema } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';
import type { Post, PostDetail, PostPage } from '@/schemas/community';

export async function getPostAction(id: string): Promise<ApiResponse<PostDetail>> {
  return postService.getPost(id);
}

export async function getUserPostsAction(userId: string, page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return postService.getUserPosts(userId, page, size);
}

export async function createPostAction(input: unknown): Promise<ApiResponse<Post>> {
  const parsed = CreatePostSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return postService.createPost(parsed.data);
}

export async function deletePostAction(id: string): Promise<ApiResponse<void>> {
  return postService.deletePost(id);
}
