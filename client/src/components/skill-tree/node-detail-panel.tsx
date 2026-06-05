"use client";

import { useState } from "react";
import { X } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { LessonPlayer } from "@/components/lessons/lesson-player";
import { QuizRunner } from "@/components/quiz/quiz-runner";
import { nodeStatus } from "@/domain/skill-tree";
import type { QuizResult, SkillNode } from "@/schemas/skill-tree";

/**
 * Detail panel for a selected skill node (issue #22): description, lessons
 * (with the embedded player), quizzes, and unlock/complete actions.
 */
export function NodeDetailPanel({
  node,
  pending,
  onClose,
  onUnlock,
  onComplete,
  onQuizPassed,
}: {
  node: SkillNode;
  pending: boolean;
  onClose: () => void;
  onUnlock: () => void;
  onComplete: () => void;
  onQuizPassed: (result: QuizResult) => void;
}) {
  const status = nodeStatus(node);
  const unlocked = status === "in-progress" || status === "completed";
  const completed = status === "completed";
  const percent = node.progress?.progressPercent ?? 0;

  const firstLessonId = node.lessons[0]?.id ?? null;
  const [activeLessonId, setActiveLessonId] = useState<string | null>(firstLessonId);
  const activeLesson =
    node.lessons.find((lesson) => lesson.id === activeLessonId) ?? node.lessons[0] ?? null;

  return (
    <div className="flex h-full w-full max-w-lg flex-col bg-background shadow-xl">
      <header className="flex items-start justify-between gap-3 border-b border-border p-5">
        <div className="min-w-0">
          <p className="text-xs uppercase tracking-wide text-muted-foreground">
            Tier {node.tier} &middot; {node.xpReward} XP
          </p>
          <h2 className="truncate text-lg font-semibold">{node.title}</h2>
        </div>
        <Button variant="ghost" size="icon" onClick={onClose} aria-label="Close panel">
          <X className="h-5 w-5" />
        </Button>
      </header>

      <div className="flex-1 space-y-6 overflow-y-auto p-5">
        {node.description ? (
          <p className="text-sm text-muted-foreground">{node.description}</p>
        ) : null}

        {!unlocked ? (
          status === "available" ? (
            <div className="rounded-md border border-dashed border-border p-4 text-sm">
              <p className="mb-3">Unlock this skill to access its lessons and quizzes.</p>
              <Button onClick={onUnlock} disabled={pending}>
                {pending ? "Unlocking…" : "Unlock skill"}
              </Button>
            </div>
          ) : (
            <div className="rounded-md border border-dashed border-border p-4 text-sm text-muted-foreground">
              Complete the previous tier to unlock this skill.
            </div>
          )
        ) : (
          <>
            <div className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="font-medium">Progress</span>
                <span className="text-muted-foreground">{percent}%</span>
              </div>
              <Progress value={percent} indicatorClassName={completed ? "bg-success" : undefined} />
            </div>

            {node.lessons.length > 0 ? (
              <section className="space-y-3">
                <h3 className="text-sm font-semibold">Lessons</h3>
                {node.lessons.length > 1 ? (
                  <div className="flex flex-wrap gap-2">
                    {node.lessons.map((lesson) => (
                      <Button
                        key={lesson.id}
                        size="sm"
                        variant={activeLesson?.id === lesson.id ? "default" : "outline"}
                        onClick={() => setActiveLessonId(lesson.id)}
                      >
                        {lesson.title}
                      </Button>
                    ))}
                  </div>
                ) : null}
                {activeLesson ? (
                  <LessonPlayer key={activeLesson.id} lesson={activeLesson} initialWatched={false} />
                ) : null}
              </section>
            ) : null}

            {node.quizzes.length > 0 ? (
              <section className="space-y-3">
                <h3 className="text-sm font-semibold">Quizzes</h3>
                <div className="space-y-3">
                  {node.quizzes.map((quiz) => (
                    <QuizRunner key={quiz.id} quiz={quiz} onPassed={onQuizPassed} />
                  ))}
                </div>
              </section>
            ) : null}

            {node.lessons.length === 0 && node.quizzes.length === 0 ? (
              <p className="text-sm text-muted-foreground">No content yet for this skill.</p>
            ) : null}
          </>
        )}
      </div>

      {unlocked && !completed ? (
        <footer className="border-t border-border p-5">
          <Button variant="success" className="w-full" onClick={onComplete} disabled={pending}>
            {pending ? "Saving…" : "Mark skill complete"}
          </Button>
        </footer>
      ) : null}
      {completed ? (
        <footer className="flex items-center gap-2 border-t border-border p-5">
          <Badge variant="success">Skill completed</Badge>
          <span className="text-sm text-muted-foreground">Nice work, chef!</span>
        </footer>
      ) : null}
    </div>
  );
}
