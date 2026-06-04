import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { formatXp, ordinalRank } from "@/domain/progression";
import type { LeaderboardEntry } from "@/schemas/progression";

/**
 * Leaderboard list (issue #22): ranked chefs by XP, optionally highlighting the
 * current user.
 */
export function Leaderboard({
  entries,
  title = "Leaderboard",
  currentUserId,
}: {
  entries: LeaderboardEntry[];
  title?: string;
  currentUserId?: string | null;
}) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent className="p-0">
        {entries.length === 0 ? (
          <p className="px-5 pb-5 text-sm text-muted-foreground">No ranked chefs yet.</p>
        ) : (
          <ul className="divide-y divide-border">
            {entries.map((entry) => {
              const isMe = currentUserId != null && currentUserId === entry.userId;
              return (
                <li
                  key={entry.userId}
                  className={cn("flex items-center gap-3 px-5 py-3", isMe && "bg-muted/50")}
                >
                  <span className="w-8 shrink-0 text-sm font-semibold text-muted-foreground">
                    {ordinalRank(entry.rank)}
                  </span>
                  <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-primary/10 text-sm font-semibold text-primary">
                    {entry.username.charAt(0).toUpperCase()}
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium">
                      {entry.username}
                      {isMe ? " (you)" : ""}
                    </p>
                    <p className="text-xs text-muted-foreground">Level {entry.level}</p>
                  </div>
                  <Badge variant="muted">{formatXp(entry.globalXp)} XP</Badge>
                </li>
              );
            })}
          </ul>
        )}
      </CardContent>
    </Card>
  );
}
