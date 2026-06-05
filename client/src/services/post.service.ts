import * as postsApi from '@/external/posts';
import type { Post, PostDetail, PostPage, CreatePostInput } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';

export async function getPost(id: string): Promise<ApiResponse<PostDetail>> {
  return postsApi.getPost(id);
}

export async function getUserPosts(userId: string, page: number, size: number): Promise<ApiResponse<PostPage>> {
  return postsApi.getUserPosts(userId, page, size);
}

export async function createPost(input: CreatePostInput): Promise<ApiResponse<Post>> {
  return postsApi.createPost(input);
}

export async function updatePost(id: string, input: CreatePostInput): Promise<ApiResponse<Post>> {
  return postsApi.updatePost(id, input);
}

export async function deletePost(id: string): Promise<ApiResponse<void>> {
  return postsApi.deletePost(id);
}
