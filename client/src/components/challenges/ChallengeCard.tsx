import Link from "next/link";
import type { Challenge } from "@/schemas/challenges";
import { challengeTypeLabel, formatChallengeWindow } from "@/domain/challenges";

export function ChallengeCard({ challenge }: { challenge: Challenge }) {
  return (
    <Link
      href={`/challenges/${challenge.id}`}
      className="block rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition-colors hover:border-orange-300"
    >
      <div className="flex items-start justify-between gap-2">
        <h2 className="font-semibold text-gray-900">{challenge.title}</h2>
        {challenge.active && (
          <span className="shrink-0 rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
            Active
          </span>
        )}
      </div>
      <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-gray-500">
        <span className="rounded-full bg-orange-100 px-2 py-0.5 font-medium text-orange-700">
          {challengeTypeLabel(challenge.type)}
        </span>
        <span>+{challenge.xpReward} XP</span>
        <span>·</span>
        <span>{formatChallengeWindow(challenge.startDate, challenge.endDate)}</span>
        <span>·</span>
        <span>
          {challenge.submissionCount}{" "}
          {challenge.submissionCount === 1 ? "entry" : "entries"}
        </span>
      </div>
    </Link>
  );
}
