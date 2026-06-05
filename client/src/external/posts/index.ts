import { http } from '@/external/http';
import type { ApiResponse } from '@/schemas/api';
import type { Post, PostDetail, PostPage, CreatePostInput } from '@/schemas/community';

export function getPost(id: string): Promise<ApiResponse<PostDetail>> {
  return http({ method: 'GET', path: `/api/posts/${id}` });
}

export function getUserPosts(userId: string, page = 0, size = 20): Promise<ApiResponse<PostPage>> {
  return http({ method: 'GET', path: `/api/posts?userId=${userId}&page=${page}&size=${size}` });
}

export function createPost(input: CreatePostInput): Promise<ApiResponse<Post>> {
  return http({ method: 'POST', path: '/api/posts', options: { body: input } });
}

export function updatePost(id: string, input: CreatePostInput): Promise<ApiResponse<Post>> {
  return http({ method: 'PUT', path: `/api/posts/${id}`, options: { body: input } });
}

export function deletePost(id: string): Promise<ApiResponse<void>> {
  return http({ method: 'DELETE', path: `/api/posts/${id}` });
}
