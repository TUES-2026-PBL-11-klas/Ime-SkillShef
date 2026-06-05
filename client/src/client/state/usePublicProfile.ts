'use client';

import { useState, useEffect, useCallback } from 'react';
import * as userActions from '@/client/actions/user.actions';
import * as socialActions from '@/client/actions/social.actions';
import type { PublicProfile } from '@/schemas/user';
import type { FollowCounts } from '@/schemas/social';

type Status = 'idle' | 'loading' | 'error';

/**
 * Public-profile state for the `/users/[userId]` page: loads the profile and
 * its follower/following counts together.
 */
export function usePublicProfile(userId: string) {
  const [profile, setProfile] = useState<PublicProfile | null>(null);
  const [counts, setCounts] = useState<FollowCounts | null>(null);
  const [status, setStatus] = useState<Status>('loading');
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setStatus('loading');
    const [profileRes, countsRes] = await Promise.all([
      userActions.getPublicProfile(userId),
      socialActions.getCounts(userId),
    ]);
    if (!profileRes.success) {
      setError(profileRes.error);
      setStatus('error');
      return;
    }
    setProfile(profileRes.data);
    if (countsRes.success) setCounts(countsRes.data);
    setStatus('idle');
  }, [userId]);

  useEffect(() => {
    load();
  }, [load]);

  return { profile, counts, status, error };
}
