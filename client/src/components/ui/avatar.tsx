import { cn } from '@/lib/utils';

interface AvatarProps {
  src?: string | null;
  alt: string;
  /** Falls back to this initial(s) when no image is set. */
  fallback: string;
  size?: number;
  className?: string;
}

/**
 * Avatar primitive: renders the image when present, otherwise an initials
 * placeholder. Plain <img> keeps it usable with arbitrary CDN URLs.
 */
export function Avatar({ src, alt, fallback, size = 48, className }: AvatarProps) {
  const dimension = { width: size, height: size };
  return (
    <span
      style={dimension}
      className={cn(
        'inline-flex shrink-0 items-center justify-center overflow-hidden rounded-full',
        'bg-muted text-muted-foreground font-semibold select-none',
        className,
      )}
    >
      {src ? (
        // eslint-disable-next-line @next/next/no-img-element
        <img src={src} alt={alt} style={dimension} className="h-full w-full object-cover" />
      ) : (
        <span style={{ fontSize: size * 0.4 }}>{fallback.slice(0, 2).toUpperCase()}</span>
      )}
    </span>
  );
}
