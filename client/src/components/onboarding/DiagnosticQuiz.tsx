'use client';

import { useOnboarding } from '@/client/state/useOnboarding';
import { LEVEL_COPY } from '@/domain/onboarding';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

/**
 * Onboarding diagnostic: one question per step, then a recommended starting
 * level. Reads everything from the useOnboarding hook (no logic here).
 */
export function DiagnosticQuiz() {
  const {
    questions,
    answers,
    step,
    total,
    recommendedLevel,
    answer,
    next,
    back,
    finish,
  } = useOnboarding();

  const onResults = step >= total;
  const progress = Math.round(((onResults ? total : step) / total) * 100);

  return (
    <div className="w-full max-w-lg">
      <div className="mb-6">
        <div className="flex justify-between text-xs text-muted-foreground mb-1.5">
          <span>{onResults ? 'Done' : `Question ${step + 1} of ${total}`}</span>
          <span>{progress}%</span>
        </div>
        <div className="h-2 w-full rounded-full bg-muted overflow-hidden">
          <div
            className="h-full bg-orange-500 transition-all"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      {onResults ? (
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">
              You&apos;re starting as: {LEVEL_COPY[recommendedLevel].title}
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-sm text-muted-foreground">
              {LEVEL_COPY[recommendedLevel].description}
            </p>
            <div className="flex gap-3">
              <Button variant="outline" onClick={back} className="flex-1">
                Back
              </Button>
              <Button onClick={finish} className="flex-1">
                Start cooking
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">{questions[step]!.prompt}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="space-y-2">
              {questions[step]!.options.map((opt) => {
                const selected = answers[questions[step]!.id] === opt.value;
                return (
                  <button
                    key={opt.value}
                    type="button"
                    onClick={() => answer(questions[step]!.id, opt.value)}
                    className={[
                      'w-full text-left rounded-lg border px-4 py-3 text-sm transition-colors',
                      selected
                        ? 'border-orange-500 bg-orange-50 text-orange-900'
                        : 'border-border hover:bg-muted',
                    ].join(' ')}
                  >
                    {opt.label}
                  </button>
                );
              })}
            </div>
            <div className="flex gap-3 pt-2">
              {step > 0 && (
                <Button variant="outline" onClick={back} className="flex-1">
                  Back
                </Button>
              )}
              <Button
                onClick={next}
                disabled={!answers[questions[step]!.id]}
                className="flex-1"
              >
                {step + 1 === total ? 'See result' : 'Next'}
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
