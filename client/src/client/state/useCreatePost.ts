'use client';

import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import * as postActions from '@/client/actions/post.actions';
import type { CreatePostInput } from '@/schemas/community';

export function useCreatePost() {
  const router = useRouter();
  const [status, setStatus] = useState<'idle' | 'submitting' | 'error'>('idle');
  const [error, setError] = useState<string | null>(null);

  const submit = useCallback(async (input: CreatePostInput) => {
    setStatus('submitting');
    setError(null);
    const res = await postActions.createPost(input);
    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return;
    }
    router.push(`/posts/${res.data.id}`);
  }, [router]);

  return { status, error, submit };
}
