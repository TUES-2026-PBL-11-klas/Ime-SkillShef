'use client';

import { useState, useEffect, useCallback } from 'react';
import * as userActions from '@/client/actions/user.actions';
import type { Profile, UpdateProfileInput } from '@/schemas/user';

type Status = 'idle' | 'loading' | 'saving' | 'error';

/**
 * Own-profile state: loads the authenticated user's profile and exposes a
 * save handler (used by the edit form and settings page).
 */
export function useProfile() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [status, setStatus] = useState<Status>('loading');
  const [error, setError] = useState<string | null>(null);
  const [savedAt, setSavedAt] = useState<number | null>(null);

  const load = useCallback(async () => {
    setStatus('loading');
    const res = await userActions.getOwnProfile();
    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return;
    }
    setProfile(res.data);
    setStatus('idle');
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const save = useCallback(async (input: UpdateProfileInput) => {
    setStatus('saving');
    setError(null);
    const res = await userActions.updateOwnProfile(input);
    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return false;
    }
    setProfile(res.data);
    setSavedAt(Date.now());
    setStatus('idle');
    return true;
  }, []);

  return { profile, status, error, savedAt, save, reload: load };
}
