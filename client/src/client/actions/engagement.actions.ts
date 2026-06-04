import {
  likePostAction,
  unlikePostAction,
  addCommentAction,
  deleteCommentAction,
  getCommentsAction,
} from '@/actions/engagement.actions';
import type { ApiResponse } from '@/schemas/api';
import type { Comment, CommentPage, AddCommentInput } from '@/schemas/community';

export function likePost(postId: string): Promise<ApiResponse<void>> {
  return likePostAction(postId);
}

export function unlikePost(postId: string): Promise<ApiResponse<void>> {
  return unlikePostAction(postId);
}

export function getComments(postId: string, page: number, size: number): Promise<ApiResponse<CommentPage>> {
  return getCommentsAction(postId, page, size);
}

export function addComment(postId: string, input: AddCommentInput): Promise<ApiResponse<Comment>> {
  return addCommentAction(postId, input);
}

export function deleteComment(postId: string, commentId: string): Promise<ApiResponse<void>> {
  return deleteCommentAction(postId, commentId);
}
