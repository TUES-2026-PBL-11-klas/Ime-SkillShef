import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { formatXp } from "@/domain/progression";
import type { LevelProgressResponse } from "@/schemas/progression";

/**
 * XP/level widget (issue #22): shows the current level, total XP and progress
 * toward the next level.
 */
export function XpWidget({
  progression,
  rank,
}: {
  progression: LevelProgressResponse | null;
  rank?: number | null;
}) {
  if (!progression) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Your progress</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">Sign in to track your XP and level.</p>
        </CardContent>
      </Card>
    );
  }

  const { level, globalXp, xpIntoLevel, xpToNextLevel, progressPercent } = progression;
  const xpThisLevel = xpIntoLevel + xpToNextLevel;

  return (
    <Card>
      <CardHeader className="flex-row items-center justify-between space-y-0">
        <CardTitle>Level {level}</CardTitle>
        {typeof rank === "number" ? <Badge variant="accent">Rank #{rank}</Badge> : null}
      </CardHeader>
      <CardContent className="space-y-3">
        <div className="flex items-center justify-between text-sm">
          <span className="text-muted-foreground">Total XP</span>
          <span className="font-semibold">{formatXp(globalXp)}</span>
        </div>
        <Progress value={progressPercent} indicatorClassName="bg-accent" />
        <p className="text-xs text-muted-foreground">
          {formatXp(xpIntoLevel)} / {formatXp(xpThisLevel)} XP &middot; {formatXp(xpToNextLevel)} to level{" "}
          {level + 1}
        </p>
      </CardContent>
    </Card>
  );
}
