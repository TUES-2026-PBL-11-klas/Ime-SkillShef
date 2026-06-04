"use client";

import { useQuizRunner } from "@/client/state/use-quiz-runner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { formatXp } from "@/domain/progression";
import type { QuizResult, QuizSummary } from "@/schemas/skill-tree";

/**
 * Quiz UI (issue #22): renders a question with selectable options and submits
 * the answer. The correct answer is never present client-side — scoring happens
 * server-side and only the result is returned.
 */
export function QuizRunner({
  quiz,
  initiallyPassed = false,
  onPassed,
}: {
  quiz: QuizSummary;
  initiallyPassed?: boolean;
  onPassed?: (result: QuizResult) => void;
}) {
  const { selected, setSelected, result, submitting, error, submit, reset } = useQuizRunner(
    quiz.id,
    onPassed,
  );

  const passed = initiallyPassed || result?.correct === true;
  const answered = result !== null;

  return (
    <div className="rounded-md border border-border p-4">
      <div className="mb-3 flex items-start justify-between gap-3">
        <p className="font-medium">{quiz.question}</p>
        {passed ? <Badge variant="success">Passed</Badge> : null}
      </div>

      <div className="space-y-2">
        {quiz.options.map((option) => {
          const isSelected = selected === option;
          const showCorrect = answered && result?.correct === true && isSelected;
          const showWrong = answered && result?.correct === false && isSelected;
          return (
            <label
              key={option}
              className={cn(
                "flex cursor-pointer items-center gap-3 rounded-md border px-3 py-2 text-sm transition-colors",
                isSelected ? "border-primary bg-primary/5" : "border-border hover:bg-muted",
                showCorrect && "border-success bg-success/10",
                showWrong && "border-destructive bg-destructive/10",
              )}
            >
              <input
                type="radio"
                name={`quiz-${quiz.id}`}
                className="accent-primary"
                value={option}
                checked={isSelected}
                disabled={submitting || passed}
                onChange={() => setSelected(option)}
              />
              <span>{option}</span>
            </label>
          );
        })}
      </div>

      {error ? <p className="mt-2 text-sm text-destructive">{error}</p> : null}

      {result ? (
        <div className="mt-3 flex items-center gap-3">
          <Badge variant={result.correct ? "success" : "warning"}>
            {result.correct ? "Correct!" : "Not quite"}
          </Badge>
          {result.awardedXp > 0 ? (
            <span className="text-sm text-muted-foreground">+{formatXp(result.awardedXp)} XP</span>
          ) : null}
        </div>
      ) : null}

      <div className="mt-4 flex gap-2">
        {!passed ? (
          <Button size="sm" onClick={() => void submit()} disabled={submitting || !selected}>
            {submitting ? "Checking…" : "Submit answer"}
          </Button>
        ) : null}
        {answered && result?.correct === false ? (
          <Button size="sm" variant="ghost" onClick={reset} disabled={submitting}>
            Try again
          </Button>
        ) : null}
      </div>
    </div>
  );
}
