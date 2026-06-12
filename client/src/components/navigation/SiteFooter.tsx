'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

/**
 * Lightweight site footer. UI-only; hidden on the focused auth/onboarding flows
 * so it mirrors the header's surface.
 */

const HIDDEN_PREFIXES = ['/login', '/signup', '/reset', '/onboarding'];

const FOOTER_LINKS = [
  { href: '/skills', label: 'Skill Tree' },
  { href: '/challenges', label: 'Challenges' },
  { href: '/feed', label: 'Community' },
  { href: '/assistant', label: 'AI Chef' },
] as const;

export function SiteFooter() {
  const pathname = usePathname();

  if (pathname === '/' || HIDDEN_PREFIXES.some((p) => pathname.startsWith(p))) {
    return null;
  }

  return (
    <footer className="border-t border-border bg-muted/30">
      <div className="mx-auto flex max-w-5xl flex-col items-center justify-between gap-4 px-4 py-6 text-sm text-muted-foreground sm:flex-row">
        <p>© {new Date().getFullYear()} SkillChef. Learn to cook, one skill at a time.</p>
        <nav className="flex flex-wrap items-center gap-4" aria-label="Footer">
          {FOOTER_LINKS.map((link) => (
            <Link key={link.href} href={link.href} className="hover:text-foreground transition-colors">
              {link.label}
            </Link>
          ))}
        </nav>
      </div>
    </footer>
  );
}
