'use client';

import { usePublicProfile } from '@/client/state/usePublicProfile';
import { Avatar } from '@/components/ui/avatar';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { FollowButton } from '@/components/profile/FollowButton';
import { ProfileStats } from '@/components/profile/ProfileStats';

/** Public profile page body for `/users/[userId]`. */
export function PublicProfileView({ userId }: { userId: string }) {
  const { profile, counts, status, error } = usePublicProfile(userId);

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
    return <p className="text-sm text-destructive">{error ?? 'Profile not found.'}</p>;
  }

  return (
    <Card>
      <CardContent className="pt-5 space-y-5">
        <div className="flex items-start justify-between gap-4">
          <div className="flex gap-4 items-center">
            <Avatar src={profile.avatarUrl} alt={profile.username} fallback={profile.username} size={64} />
            <div>
              <h1 className="text-xl font-bold text-foreground">{profile.username}</h1>
              <p className="text-sm text-muted-foreground">Level {profile.level}</p>
            </div>
          </div>
          <FollowButton userId={userId} />
        </div>

        {profile.bio && <p className="text-sm text-foreground whitespace-pre-line">{profile.bio}</p>}

        <ProfileStats
          stats={[
            { label: 'XP', value: profile.globalXp.toLocaleString() },
            { label: 'Level', value: profile.level },
            { label: 'Followers', value: counts?.followers ?? 0 },
            { label: 'Following', value: counts?.following ?? 0 },
          ]}
        />
      </CardContent>
    </Card>
  );
}
