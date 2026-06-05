import Link from 'next/link';
import { AuthShell } from '@/components/auth/AuthShell';
import { RequestResetForm } from '@/components/auth/RequestResetForm';
import { ConfirmResetForm } from '@/components/auth/ConfirmResetForm';

export const metadata = { title: 'Reset password — SkillChef' };

interface Props {
  searchParams: Promise<{ token?: string }>;
}

/**
 * Single reset route handling both steps: with a `?token=` (from the email
 * link) it shows the "set new password" form, otherwise the "request link" form.
 */
export default async function ResetPasswordPage({ searchParams }: Props) {
  const { token } = await searchParams;

  return (
    <AuthShell
      title={token ? 'Set a new password' : 'Reset your password'}
      description={
        token
          ? 'Choose a new password for your account.'
          : "Enter your email and we'll send you a reset link."
      }
      footer={
        <Link href="/login" className="text-orange-600 hover:underline">
          Back to sign in
        </Link>
      }
    >
      {token ? <ConfirmResetForm token={token} /> : <RequestResetForm />}
    </AuthShell>
  );
}
