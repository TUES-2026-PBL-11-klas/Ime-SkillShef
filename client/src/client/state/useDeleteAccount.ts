'use client';

import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import * as userActions from '@/client/actions/user.actions';
import * as authActions from '@/client/actions/auth.actions';

type Status = 'idle' | 'deleting' | 'error';

/** Deletes the authenticated user's account, clears the session, and exits. */
export function useDeleteAccount() {
  const router = useRouter();
  const [status, setStatus] = useState<Status>('idle');
  const [error, setError] = useState<string | null>(null);

  const deleteAccount = useCallback(async () => {
    setStatus('deleting');
    setError(null);
    const res = await userActions.deleteOwnProfile();
    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return;
    }
    // Drop the now-orphaned session cookies before leaving.
    await authActions.logout();
    router.push('/signup');
    router.refresh();
  }, [router]);

  return { status, error, deleteAccount };
}
