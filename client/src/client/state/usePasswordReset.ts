'use client';

import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import * as authActions from '@/client/actions/auth.actions';
import type { ConfirmResetInput, RequestResetInput } from '@/schemas/auth';

type Status = 'idle' | 'submitting' | 'success' | 'error';

/** UI orchestration for the password-reset request and confirm steps. */
export function usePasswordReset() {
  const router = useRouter();
  const [status, setStatus] = useState<Status>('idle');
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const requestReset = useCallback(async (input: RequestResetInput) => {
    setStatus('submitting');
    setError(null);
    const res = await authActions.requestPasswordReset(input);
    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return;
    }
    // Always present a neutral confirmation so we never leak which emails exist.
    setMessage('If an account exists for that email, a reset link is on its way.');
    setStatus('success');
  }, []);

  const confirmReset = useCallback(
    async (input: ConfirmResetInput) => {
      setStatus('submitting');
      setError(null);
      const res = await authActions.confirmPasswordReset(input);
      if (!res.success) {
        setError(res.error);
        setStatus('error');
        return;
      }
      setMessage('Your password has been updated. You can now sign in.');
      setStatus('success');
      setTimeout(() => router.push('/login'), 1500);
    },
    [router],
  );

  return { status, error, message, requestReset, confirmReset };
}
