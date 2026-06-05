import Link from 'next/link';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

interface AuthShellProps {
  title: string;
  description?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
}

/** Centered card layout shared by every auth page (login/signup/reset). */
export function AuthShell({ title, description, children, footer }: AuthShellProps) {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center px-4 py-12 bg-muted/30">
      <div className="w-full max-w-md">
        <Link href="/" className="block text-center mb-6 text-2xl font-bold text-orange-600">
          SkillChef
        </Link>
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">{title}</CardTitle>
            {description && <CardDescription>{description}</CardDescription>}
          </CardHeader>
          <CardContent>{children}</CardContent>
        </Card>
        {footer && <div className="mt-6 text-center text-sm text-muted-foreground">{footer}</div>}
      </div>
    </main>
  );
}
