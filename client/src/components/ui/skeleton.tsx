import * as React from "react";
import { cn } from "@/lib/utils";

/**
 * Skeleton placeholder (issue #22) for loading states.
 */
export function Skeleton({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return <div className={cn("animate-pulse rounded-md bg-muted", className)} {...props} />;
}
