'use server';

import * as engagementService from '@/services/engagement.service';
import { AddCommentSchema } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';
import type { Comment, CommentPage, EngagementSummary } from '@/schemas/community';

export async function getEngagementAction(postId: string): Promise<ApiResponse<EngagementSummary>> {
  return engagementService.getEngagement(postId);
}

export async function likePostAction(postId: string): Promise<ApiResponse<void>> {
  return engagementService.likePost(postId);
}

export async function unlikePostAction(postId: string): Promise<ApiResponse<void>> {
  return engagementService.unlikePost(postId);
}

export async function getCommentsAction(postId: string, page = 0, size = 20): Promise<ApiResponse<CommentPage>> {
  return engagementService.getComments(postId, page, size);
}

export async function addCommentAction(postId: string, input: unknown): Promise<ApiResponse<Comment>> {
  const parsed = AddCommentSchema.safeParse(input);
  if (!parsed.success) {
    return { success: false, error: parsed.error.issues[0]?.message ?? 'Invalid input' };
  }
  return engagementService.addComment(postId, parsed.data.content);
}

export async function deleteCommentAction(postId: string, commentId: string): Promise<ApiResponse<void>> {
  return engagementService.deleteComment(postId, commentId);
}
