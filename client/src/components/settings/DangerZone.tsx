'use client';

import { useState } from 'react';
import { useDeleteAccount } from '@/client/state/useDeleteAccount';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

/** Account deletion section with a confirm step to avoid accidental loss. */
export function DangerZone() {
  const { status, error, deleteAccount } = useDeleteAccount();
  const [confirming, setConfirming] = useState(false);

  return (
    <Card className="border-destructive/40">
      <CardHeader>
        <CardTitle className="text-destructive">Delete account</CardTitle>
        <CardDescription>
          Permanently delete your account, profile, and progress. This cannot be undone.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {error && <p className="text-sm text-destructive">{error}</p>}
        {confirming ? (
          <div className="flex gap-3">
            <Button variant="outline" onClick={() => setConfirming(false)}>
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={deleteAccount}
              disabled={status === 'deleting'}
            >
              {status === 'deleting' ? 'Deleting…' : 'Yes, delete my account'}
            </Button>
          </div>
        ) : (
          <Button variant="destructive" onClick={() => setConfirming(true)}>
            Delete account
          </Button>
        )}
      </CardContent>
    </Card>
  );
}
