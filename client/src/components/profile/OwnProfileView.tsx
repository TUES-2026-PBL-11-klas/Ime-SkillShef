'use client';

import Link from 'next/link';
import { useProfile } from '@/client/state/useProfile';
import { Avatar } from '@/components/ui/avatar';
import { buttonVariants } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { ProfileStats } from '@/components/profile/ProfileStats';

/** The authenticated user's own profile page body. */
export function OwnProfileView() {
  const { profile, status, error } = useProfile();

  if (status === 'loading') {
    return (
      <Card>
        <CardContent className="pt-5 flex gap-4 items-center">
          <Skeleton className="h-16 w-16 rounded-full" />
          <div className="space-y-2">
            <Skeleton className="h-5 w-40" />
            <Skeleton className="h-4 w-24" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (status === 'error' || !profile) {
    return <p className="text-sm text-destructive">{error ?? 'Could not load your profile.'}</p>;
  }

  return (
    <Card>
      <CardContent className="pt-5 space-y-5">
        <div className="flex items-start justify-between gap-4">
          <div className="flex gap-4 items-center">
            <Avatar src={profile.avatarUrl} alt={profile.username} fallback={profile.username} size={64} />
            <div>
              <h1 className="text-xl font-bold text-foreground">{profile.username}</h1>
              <p className="text-sm text-muted-foreground">{profile.email}</p>
            </div>
          </div>
          <Link href="/settings" className={buttonVariants({ variant: 'outline' })}>
            Edit profile
          </Link>
        </div>

        {profile.bio && <p className="text-sm text-foreground whitespace-pre-line">{profile.bio}</p>}

        <ProfileStats
          stats={[
            { label: 'XP', value: profile.globalXp.toLocaleString() },
            { label: 'Level', value: profile.level },
          ]}
        />
      </CardContent>
    </Card>
  );
}
