'use client';

import { useState, useCallback } from 'react';
import * as userActions from '@/client/actions/user.actions';

type Status = 'idle' | 'uploading' | 'error';

/** Handles avatar file selection + upload, returning the new CDN URL. */
export function useAvatarUpload(onUploaded?: (avatarUrl: string) => void) {
  const [status, setStatus] = useState<Status>('idle');
  const [error, setError] = useState<string | null>(null);

  const upload = useCallback(
    async (file: File) => {
      setStatus('uploading');
      setError(null);
      const form = new FormData();
      form.append('file', file);
      const res = await userActions.uploadAvatar(form);
      if (!res.success) {
        setError(res.error);
        setStatus('error');
        return;
      }
      setStatus('idle');
      onUploaded?.(res.data.avatarUrl);
    },
    [onUploaded],
  );

  return { status, error, upload };
}
