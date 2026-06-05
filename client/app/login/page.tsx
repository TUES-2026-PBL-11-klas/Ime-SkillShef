import Link from 'next/link';
import { AuthShell } from '@/components/auth/AuthShell';
import { LoginForm } from '@/components/auth/LoginForm';

export const metadata = { title: 'Sign in — SkillChef' };

export default function LoginPage() {
  return (
    <AuthShell
      title="Welcome back"
      description="Sign in to continue your culinary journey."
      footer={
        <>
          New to SkillChef?{' '}
          <Link href="/signup" className="text-orange-600 hover:underline">
            Create an account
          </Link>
        </>
      }
    >
      <LoginForm />
    </AuthShell>
  );
}
