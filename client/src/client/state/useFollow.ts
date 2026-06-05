'use client';

import { useState, useEffect, useCallback } from 'react';
import * as socialActions from '@/client/actions/social.actions';

/**
 * Follow/unfollow state for a target user, with optimistic toggling. Loads the
 * initial "is following" status on mount.
 */
export function useFollow(userId: string) {
  const [following, setFollowing] = useState<boolean | null>(null);
  const [pending, setPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    socialActions.isFollowing(userId).then((res) => {
      if (active && res.success) setFollowing(res.data.following);
    });
    return () => {
      active = false;
    };
  }, [userId]);

  const toggle = useCallback(async () => {
    if (pending || following === null) return;
    const next = !following;
    setFollowing(next); // optimistic
    setPending(true);
    setError(null);
    const res = next
      ? await socialActions.follow(userId)
      : await socialActions.unfollow(userId);
    if (!res.success) {
      setFollowing(!next); // rollback
      setError(res.error);
    }
    setPending(false);
  }, [following, pending, userId]);

  return { following, pending, error, toggle };
}
