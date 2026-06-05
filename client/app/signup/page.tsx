import Link from 'next/link';
import { AuthShell } from '@/components/auth/AuthShell';
import { SignupForm } from '@/components/auth/SignupForm';

export const metadata = { title: 'Create account — SkillChef' };

export default function SignupPage() {
  return (
    <AuthShell
      title="Create your account"
      description="Start building real cooking skills, one technique at a time."
      footer={
        <>
          Already have an account?{' '}
          <Link href="/login" className="text-orange-600 hover:underline">
            Sign in
          </Link>
        </>
      }
    >
      <SignupForm />
    </AuthShell>
  );
}
