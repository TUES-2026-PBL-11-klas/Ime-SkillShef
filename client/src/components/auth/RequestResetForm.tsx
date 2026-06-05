'use client';

import { useState } from 'react';
import { usePasswordReset } from '@/client/state/usePasswordReset';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

/** Step 1 of password reset: request a reset link by email. */
export function RequestResetForm() {
  const { status, error, message, requestReset } = usePasswordReset();
  const [email, setEmail] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    requestReset({ email });
  }

  if (status === 'success' && message) {
    return <p className="text-sm text-foreground">{message}</p>;
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
          placeholder="you@example.com"
        />
      </div>

      {error && <p className="text-sm text-destructive">{error}</p>}

      <Button type="submit" className="w-full" disabled={status === 'submitting'}>
        {status === 'submitting' ? 'Sending…' : 'Send reset link'}
      </Button>
    </form>
  );
}
