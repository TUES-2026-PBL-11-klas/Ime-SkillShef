'use client';

import { useRef, useState } from 'react';
import { useAvatarUpload } from '@/client/state/useAvatarUpload';
import { Avatar } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';

interface AvatarUploadProps {
  username: string;
  avatarUrl: string | null;
}

/** Avatar preview + file picker that uploads through the User Service. */
export function AvatarUpload({ username, avatarUrl }: AvatarUploadProps) {
  const [current, setCurrent] = useState<string | null>(avatarUrl);
  const { status, error, upload } = useAvatarUpload(setCurrent);
  const inputRef = useRef<HTMLInputElement>(null);

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (file) upload(file);
  }

  return (
    <div className="flex items-center gap-4">
      <Avatar src={current} alt={username} fallback={username} size={64} />
      <div className="space-y-1">
        <input
          ref={inputRef}
          type="file"
          accept="image/*"
          onChange={handleChange}
          className="hidden"
        />
        <Button
          variant="outline"
          size="sm"
          onClick={() => inputRef.current?.click()}
          disabled={status === 'uploading'}
        >
          {status === 'uploading' ? 'Uploading…' : 'Change avatar'}
        </Button>
        {error && <p className="text-xs text-destructive">{error}</p>}
      </div>
    </div>
  );
}
