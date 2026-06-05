'use client';

import { useEffect, useState } from 'react';
import { useProfile } from '@/client/state/useProfile';
import { useAuth } from '@/client/state/useAuth';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Skeleton } from '@/components/ui/skeleton';
import { AvatarUpload } from '@/components/settings/AvatarUpload';
import { DangerZone } from '@/components/settings/DangerZone';
import type { Preferences } from '@/schemas/user';

const THEMES: Preferences['theme'][] = ['light', 'dark', 'system'];
const LANGUAGES = [
  { value: 'en', label: 'English' },
  { value: 'bg', label: 'Български' },
];

/**
 * Settings page body: edit profile (username, bio, avatar), preferences
 * (notifications, theme, language), sign out, and delete account.
 */
export function SettingsPanel() {
  const { profile, status, error, savedAt, save } = useProfile();
  const { logout } = useAuth();

  const [username, setUsername] = useState('');
  const [bio, setBio] = useState('');
  const [preferences, setPreferences] = useState<Preferences>({
    notifications: true,
    theme: 'system',
    language: 'en',
  });

  // Hydrate the form once the profile loads.
  useEffect(() => {
    if (!profile) return;
    setUsername(profile.username);
    setBio(profile.bio ?? '');
    setPreferences(profile.preferences);
  }, [profile]);

  if (status === 'loading' && !profile) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-24 w-full" />
        <Skeleton className="h-48 w-full" />
      </div>
    );
  }

  if (!profile) {
    return <p className="text-sm text-destructive">{error ?? 'Could not load your settings.'}</p>;
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    save({ username, bio, preferences });
  }

  const saving = status === 'saving';

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Profile</CardTitle>
        </CardHeader>
        <CardContent className="space-y-5">
          <AvatarUpload username={profile.username} avatarUrl={profile.avatarUrl} />

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                minLength={3}
                maxLength={30}
                required
              />
            </div>
            <div>
              <Label htmlFor="bio">Bio</Label>
              <textarea
                id="bio"
                value={bio}
                onChange={(e) => setBio(e.target.value)}
                rows={4}
                maxLength={2000}
                placeholder="Tell the community about your cooking…"
                className="flex w-full rounded-md border border-border bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-1 resize-none"
              />
            </div>

            <div className="border-t border-border pt-5 space-y-5">
              <h3 className="text-sm font-semibold text-foreground">Preferences</h3>

              <label className="flex items-center justify-between gap-4 cursor-pointer">
                <span className="text-sm text-foreground">Email notifications</span>
                <input
                  type="checkbox"
                  checked={preferences.notifications}
                  onChange={(e) =>
                    setPreferences((p) => ({ ...p, notifications: e.target.checked }))
                  }
                  className="h-4 w-4 accent-orange-500"
                />
              </label>

              <div>
                <Label htmlFor="theme">Theme</Label>
                <select
                  id="theme"
                  value={preferences.theme}
                  onChange={(e) =>
                    setPreferences((p) => ({ ...p, theme: e.target.value as Preferences['theme'] }))
                  }
                  className="flex h-10 w-full rounded-md border border-border bg-background px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  {THEMES.map((t) => (
                    <option key={t} value={t}>
                      {t.charAt(0).toUpperCase() + t.slice(1)}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <Label htmlFor="language">Language</Label>
                <select
                  id="language"
                  value={preferences.language}
                  onChange={(e) =>
                    setPreferences((p) => ({ ...p, language: e.target.value }))
                  }
                  className="flex h-10 w-full rounded-md border border-border bg-background px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  {LANGUAGES.map((l) => (
                    <option key={l.value} value={l.value}>
                      {l.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {error && <p className="text-sm text-destructive">{error}</p>}
            {savedAt && status === 'idle' && (
              <p className="text-sm text-success">Saved.</p>
            )}

            <Button type="submit" disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </Button>
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Session</CardTitle>
        </CardHeader>
        <CardContent>
          <Button variant="outline" onClick={logout}>
            Sign out
          </Button>
        </CardContent>
      </Card>

      <DangerZone />
    </div>
  );
}
