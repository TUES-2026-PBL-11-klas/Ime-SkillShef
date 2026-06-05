import { Check, Lock, Play } from "lucide-react";
import { Badge, type BadgeProps } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { cn } from "@/lib/utils";
import { nodeStatus, type NodeStatus } from "@/domain/skill-tree";
import type { SkillNode } from "@/schemas/skill-tree";

/**
 * A single skill node card (issue #22): title, tier, XP reward, status and the
 * relevant primary action (unlock or open).
 */

const STATUS_BADGE: Record<NodeStatus, { label: string; variant: BadgeProps["variant"] }> = {
  completed: { label: "Completed", variant: "success" },
  "in-progress": { label: "In progress", variant: "accent" },
  available: { label: "Available", variant: "warning" },
  locked: { label: "Locked", variant: "muted" },
};

export function SkillNodeCard({
  node,
  pending,
  onOpen,
  onUnlock,
}: {
  node: SkillNode;
  pending: boolean;
  onOpen: () => void;
  onUnlock: () => void;
}) {
  const status = nodeStatus(node);
  const badge = STATUS_BADGE[status];
  const percent = node.progress?.progressPercent ?? 0;
  const locked = status === "locked";
  const showProgress = status === "in-progress" || status === "completed";

  return (
    <div
      className={cn(
        "flex flex-col gap-3 rounded-lg border border-border bg-card p-4 card-hover",
        locked && "opacity-70",
      )}
    >
      <div className="flex items-start justify-between gap-2">
        <div className="min-w-0">
          <p className="truncate font-semibold">{node.title}</p>
          <p className="text-xs text-muted-foreground">
            Tier {node.tier} &middot; {node.xpReward} XP
          </p>
        </div>
        <Badge variant={badge.variant}>{badge.label}</Badge>
      </div>

      {node.description ? (
        <p className="line-clamp-2 text-sm text-muted-foreground">{node.description}</p>
      ) : null}

      {showProgress ? (
        <Progress
          value={percent}
          indicatorClassName={status === "completed" ? "bg-success" : undefined}
        />
      ) : null}

      <div className="mt-auto flex items-center gap-2 pt-1">
        {status === "available" ? (
          <Button size="sm" onClick={onUnlock} disabled={pending}>
            {pending ? "Unlocking…" : "Unlock"}
          </Button>
        ) : null}
        {locked ? (
          <Button size="sm" variant="ghost" disabled>
            <Lock className="h-3.5 w-3.5" /> Locked
          </Button>
        ) : (
          <Button size="sm" variant="secondary" onClick={onOpen} disabled={pending}>
            <Play className="h-3.5 w-3.5" /> Open
          </Button>
        )}
        {status === "completed" ? (
          <span className="ml-auto inline-flex items-center text-success">
            <Check className="h-4 w-4" />
          </span>
        ) : null}
      </div>
    </div>
  );
}
