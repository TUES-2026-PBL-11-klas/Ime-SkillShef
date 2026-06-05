import { createPostAction, deletePostAction, getPostAction } from '@/actions/post.actions';
import type { ApiResponse } from '@/schemas/api';
import type { Post, PostDetail, CreatePostInput } from '@/schemas/community';

export function getPost(id: string): Promise<ApiResponse<PostDetail>> {
  return getPostAction(id);
}

export function createPost(input: CreatePostInput): Promise<ApiResponse<Post>> {
  return createPostAction(input);
}

export function deletePost(id: string): Promise<ApiResponse<void>> {
  return deletePostAction(id);
}
