'use client';

import { useState } from 'react';
import { usePasswordReset } from '@/client/state/usePasswordReset';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

/**
 * Step 2 of password reset: set a new password using the token from the email
 * link (passed in via the `?token=` query param).
 */
export function ConfirmResetForm({ token }: { token: string }) {
  const { status, error, message, confirmReset } = usePasswordReset();
  const [password, setPassword] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    confirmReset({ token, password });
  }

  if (status === 'success' && message) {
    return <p className="text-sm text-foreground">{message}</p>;
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <Label htmlFor="password">New password</Label>
        <Input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          autoComplete="new-password"
          placeholder="At least 8 characters"
        />
      </div>

      {error && <p className="text-sm text-destructive">{error}</p>}

      <Button type="submit" className="w-full" disabled={status === 'submitting'}>
        {status === 'submitting' ? 'Updating…' : 'Update password'}
      </Button>
    </form>
  );
}
