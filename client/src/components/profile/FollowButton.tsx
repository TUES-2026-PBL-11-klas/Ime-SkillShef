'use client';

import { useFollow } from '@/client/state/useFollow';
import { Button } from '@/components/ui/button';

/** Follow/unfollow toggle for a target user's profile. */
export function FollowButton({ userId }: { userId: string }) {
  const { following, pending, error, toggle } = useFollow(userId);

  if (following === null) {
    return (
      <Button variant="outline" disabled className="min-w-28">
        …
      </Button>
    );
  }

  return (
    <div className="flex flex-col items-end gap-1">
      <Button
        variant={following ? 'outline' : 'default'}
        onClick={toggle}
        disabled={pending}
        className="min-w-28"
      >
        {following ? 'Following' : 'Follow'}
      </Button>
      {error && <span className="text-xs text-destructive">{error}</span>}
    </div>
  );
}
