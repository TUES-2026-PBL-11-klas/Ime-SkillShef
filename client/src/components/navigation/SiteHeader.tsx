'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/client/state/useAuth';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

/**
 * Primary navigation for the app's authenticated surface. UI-only: routing
 * comes from `usePathname` and sign-out is delegated to the `useAuth` hook.
 */

const NAV_LINKS = [
  { href: '/skills', label: 'Skill Tree' },
  { href: '/challenges', label: 'Challenges' },
  { href: '/feed', label: 'Community' },
  { href: '/leaderboard', label: 'Leaderboard' },
  { href: '/assistant', label: 'AI Chef' },
] as const;

// Routes that render their own focused chrome and should not show the app nav.
const HIDDEN_PREFIXES = ['/login', '/signup', '/reset', '/onboarding'];

export function SiteHeader() {
  const pathname = usePathname();
  const { logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  if (pathname === '/' || HIDDEN_PREFIXES.some((p) => pathname.startsWith(p))) {
    return null;
  }

  const isActive = (href: string) => pathname === href || pathname.startsWith(`${href}/`);

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/80">
      <div className="mx-auto flex h-14 max-w-5xl items-center justify-between gap-4 px-4">
        <Link href="/skills" className="text-lg font-bold text-orange-600">
          SkillChef
        </Link>

        <nav className="hidden items-center gap-1 md:flex" aria-label="Primary">
          {NAV_LINKS.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={cn(
                'rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive(link.href)
                  ? 'bg-muted text-foreground'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground',
              )}
            >
              {link.label}
            </Link>
          ))}
        </nav>

        <div className="hidden items-center gap-2 md:flex">
          <Link
            href="/profile"
            className={cn(
              'rounded-md px-3 py-2 text-sm font-medium transition-colors',
              isActive('/profile')
                ? 'bg-muted text-foreground'
                : 'text-muted-foreground hover:bg-muted hover:text-foreground',
            )}
          >
            Profile
          </Link>
          <Button variant="outline" size="sm" onClick={() => logout()}>
            Sign out
          </Button>
        </div>

        <button
          type="button"
          className="inline-flex h-9 w-9 items-center justify-center rounded-md text-muted-foreground hover:bg-muted md:hidden"
          aria-label="Toggle navigation menu"
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((open) => !open)}
        >
          <span aria-hidden className="text-xl leading-none">
            {menuOpen ? '✕' : '☰'}
          </span>
        </button>
      </div>

      {menuOpen && (
        <nav
          className="flex flex-col gap-1 border-t border-border px-4 py-3 md:hidden"
          aria-label="Primary"
        >
          {[...NAV_LINKS, { href: '/profile', label: 'Profile' }].map((link) => (
            <Link
              key={link.href}
              href={link.href}
              onClick={() => setMenuOpen(false)}
              className={cn(
                'rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive(link.href)
                  ? 'bg-muted text-foreground'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground',
              )}
            >
              {link.label}
            </Link>
          ))}
          <Button
            variant="outline"
            size="sm"
            className="mt-1 self-start"
            onClick={() => {
              setMenuOpen(false);
              logout();
            }}
          >
            Sign out
          </Button>
        </nav>
      )}
    </header>
  );
}
