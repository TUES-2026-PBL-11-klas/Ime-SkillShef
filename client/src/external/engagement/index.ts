import { http } from '@/external/http';
import type { ApiResponse } from '@/schemas/api';
import type { Comment, CommentPage, EngagementSummary } from '@/schemas/community';

export function getEngagement(postId: string): Promise<ApiResponse<EngagementSummary>> {
  return http({ method: 'GET', path: `/api/posts/${postId}/engagement` });
}

export function likePost(postId: string): Promise<ApiResponse<void>> {
  return http({ method: 'POST', path: `/api/posts/${postId}/likes` });
}

export function unlikePost(postId: string): Promise<ApiResponse<void>> {
  return http({ method: 'DELETE', path: `/api/posts/${postId}/likes` });
}

export function getComments(postId: string, page = 0, size = 20): Promise<ApiResponse<CommentPage>> {
  return http({ method: 'GET', path: `/api/posts/${postId}/comments?page=${page}&size=${size}` });
}

export function addComment(postId: string, content: string): Promise<ApiResponse<Comment>> {
  return http({ method: 'POST', path: `/api/posts/${postId}/comments`, options: { body: { content } } });
}

export function deleteComment(postId: string, commentId: string): Promise<ApiResponse<void>> {
  return http({ method: 'DELETE', path: `/api/posts/${postId}/comments/${commentId}` });
}
