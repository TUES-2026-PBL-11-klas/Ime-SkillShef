import Link from 'next/link';
import { buttonVariants } from '@/components/ui/button';

export default function LandingPage() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center text-center px-4 gap-8 bg-muted/30">
      <div className="space-y-3 max-w-xl">
        <h1 className="text-4xl font-bold text-orange-600">SkillChef</h1>
        <p className="text-muted-foreground">
          Learn to cook with a structured skill tree, gamified progression, an AI chef assistant,
          and a community that grows with you.
        </p>
      </div>
      <div className="flex gap-3">
        <Link href="/signup" className={buttonVariants({ variant: 'default', size: 'lg' })}>
          Get started
        </Link>
        <Link href="/login" className={buttonVariants({ variant: 'outline', size: 'lg' })}>
          Sign in
        </Link>
      </div>
    </main>
  );
}
