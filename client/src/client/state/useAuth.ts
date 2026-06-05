'use client';

import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import * as authActions from '@/client/actions/auth.actions';
import type { LoginInput, SignupInput } from '@/schemas/auth';

type Status = 'idle' | 'submitting' | 'error';

/**
 * UI-level orchestration for login/signup/logout. Exposes ready-to-render
 * status/error plus submit handlers; navigation happens here, not in components.
 */
export function useAuth() {
  const router = useRouter();
  const [status, setStatus] = useState<Status>('idle');
  const [error, setError] = useState<string | null>(null);

  const signup = useCallback(
    async (input: SignupInput) => {
      setStatus('submitting');
      setError(null);
      const res = await authActions.signup(input);
      if (!res.success) {
        setError(res.error);
        setStatus('error');
        return;
      }
      // New users go through the diagnostic before reaching the skill tree.
      router.push('/onboarding');
      router.refresh();
    },
    [router],
  );

  const login = useCallback(
    async (input: LoginInput, redirectTo = '/skills') => {
      setStatus('submitting');
      setError(null);
      const res = await authActions.login(input);
      if (!res.success) {
        setError(res.error);
        setStatus('error');
        return;
      }
      router.push(redirectTo);
      router.refresh();
    },
    [router],
  );

  const logout = useCallback(async () => {
    setStatus('submitting');
    await authActions.logout();
    setStatus('idle');
    router.push('/login');
    router.refresh();
  }, [router]);

  return { status, error, signup, login, logout };
}
