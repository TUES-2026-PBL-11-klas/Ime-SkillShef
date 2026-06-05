import * as engagementApi from '@/external/engagement';
import type { Comment, CommentPage, EngagementSummary } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';

export async function getEngagement(postId: string): Promise<ApiResponse<EngagementSummary>> {
  return engagementApi.getEngagement(postId);
}

export async function likePost(postId: string): Promise<ApiResponse<void>> {
  return engagementApi.likePost(postId);
}

export async function unlikePost(postId: string): Promise<ApiResponse<void>> {
  return engagementApi.unlikePost(postId);
}

export async function getComments(postId: string, page: number, size: number): Promise<ApiResponse<CommentPage>> {
  return engagementApi.getComments(postId, page, size);
}

export async function addComment(postId: string, content: string): Promise<ApiResponse<Comment>> {
  return engagementApi.addComment(postId, content);
}

export async function deleteComment(postId: string, commentId: string): Promise<ApiResponse<void>> {
  return engagementApi.deleteComment(postId, commentId);
}
