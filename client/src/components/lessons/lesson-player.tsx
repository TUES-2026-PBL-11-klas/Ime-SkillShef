"use client";

import { useLessonPlayer } from "@/client/state/use-lesson-player";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { formatDuration } from "@/domain/skill-tree";
import type { LessonSummary } from "@/schemas/skill-tree";

/**
 * Lesson player (issue #22): embeds the lesson video (Mux/CDN URL) and lets the
 * learner mark it watched. For HLS (.m3u8) sources a player library such as
 * hls.js / Mux Player can be added later; native playback covers MP4 and Safari.
 */
export function LessonPlayer({
  lesson,
  initialWatched,
  onWatched,
}: {
  lesson: LessonSummary;
  initialWatched: boolean;
  onWatched?: () => void;
}) {
  const { watched, marking, error, markWatched } = useLessonPlayer(lesson, initialWatched, onWatched);

  return (
    <div className="space-y-3">
      <div className="aspect-video overflow-hidden rounded-md bg-navy">
        <video className="h-full w-full" controls preload="metadata" src={lesson.videoUrl}>
          Your browser does not support embedded video.
        </video>
      </div>
      <div className="flex items-center justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate font-medium">{lesson.title}</p>
          <p className="text-xs text-muted-foreground">{formatDuration(lesson.durationSeconds)}</p>
        </div>
        {watched ? (
          <Badge variant="success">Watched</Badge>
        ) : (
          <Button
            size="sm"
            variant="secondary"
            onClick={() => void markWatched()}
            disabled={marking}
          >
            {marking ? "Saving…" : "Mark watched"}
          </Button>
        )}
      </div>
      {lesson.description ? (
        <p className="text-sm text-muted-foreground">{lesson.description}</p>
      ) : null}
      {error ? <p className="text-sm text-destructive">{error}</p> : null}
    </div>
  );
}
