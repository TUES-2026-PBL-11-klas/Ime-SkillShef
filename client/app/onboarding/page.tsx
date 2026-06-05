import { DiagnosticQuiz } from '@/components/onboarding/DiagnosticQuiz';

export const metadata = { title: 'Get started — SkillChef' };

export default function OnboardingPage() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center px-4 py-12 bg-muted/30">
      <div className="mb-8 text-center">
        <h1 className="text-2xl font-bold text-foreground">Let&apos;s find your starting point</h1>
        <p className="text-sm text-muted-foreground mt-1">
          A few quick questions so we can tailor your skill tree.
        </p>
      </div>
      <DiagnosticQuiz />
    </main>
  );
}
