'use client';

import { useState, useCallback } from 'react';
import * as engagementActions from '@/client/actions/engagement.actions';
import type { Comment } from '@/schemas/community';

export function useLike(postId: string, initialCount: number) {
  const [likeCount, setLikeCount] = useState(initialCount);
  const [liked, setLiked] = useState(false);
  const [pending, setPending] = useState(false);

  const toggle = useCallback(async () => {
    if (pending) return;
    setPending(true);
    const res = liked
      ? await engagementActions.unlikePost(postId)
      : await engagementActions.likePost(postId);
    if (res.success) {
      setLiked(prev => !prev);
      setLikeCount(prev => liked ? prev - 1 : prev + 1);
    }
    setPending(false);
  }, [postId, liked, pending]);

  return { likeCount, liked, toggle, pending };
}

export function useComments(postId: string, initialComments: Comment[]) {
  const [comments, setComments] = useState<Comment[]>(initialComments);
  const [status, setStatus] = useState<'idle' | 'submitting' | 'error'>('idle');
  const [error, setError] = useState<string | null>(null);

  const submit = useCallback(async (content: string) => {
    setStatus('submitting');
    const res = await engagementActions.addComment(postId, { content });
    if (!res.success) { setError(res.error); setStatus('error'); return; }
    setComments(prev => [...prev, res.data]);
    setStatus('idle');
  }, [postId]);

  const remove = useCallback(async (commentId: string) => {
    const res = await engagementActions.deleteComment(postId, commentId);
    if (res.success) setComments(prev => prev.filter(c => c.id !== commentId));
  }, [postId]);

  return { comments, status, error, submit, remove };
}
