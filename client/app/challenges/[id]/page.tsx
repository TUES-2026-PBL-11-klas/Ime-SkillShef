import { notFound } from "next/navigation";
import Link from "next/link";
import { getChallenge } from "@/services/challenges";
import { SubmissionList } from "@/components/challenges/SubmissionList";
import { SubmissionUploadForm } from "@/components/challenges/SubmissionUploadForm";
import { challengeTypeLabel, formatChallengeWindow } from "@/domain/challenges";

export const dynamic = "force-dynamic";

export default async function ChallengeDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const res = await getChallenge(id);
  if (!res.success) {
    notFound();
  }
  const challenge = res.data;

  return (
    <main className="mx-auto max-w-3xl space-y-6 px-4 py-8">
      <Link href="/challenges" className="text-sm text-orange-600 hover:underline">
        ← All challenges
      </Link>

      <div>
        <div className="flex items-center gap-2">
          <h1 className="text-2xl font-bold text-gray-900">{challenge.title}</h1>
          {challenge.active && (
            <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
              Active
            </span>
          )}
        </div>
        <div className="mt-2 flex flex-wrap items-center gap-2 text-sm text-gray-500">
          <span className="rounded-full bg-orange-100 px-2 py-0.5 text-xs font-medium text-orange-700">
            {challengeTypeLabel(challenge.type)}
          </span>
          <span>+{challenge.xpReward} XP</span>
          <span>·</span>
          <span>{formatChallengeWindow(challenge.startDate, challenge.endDate)}</span>
        </div>
      </div>

      {challenge.active ? (
        <SubmissionUploadForm challengeId={challenge.id} />
      ) : (
        <p className="rounded-lg bg-gray-50 px-4 py-3 text-sm text-gray-500">
          This challenge is closed for new submissions.
        </p>
      )}

      <section className="space-y-3">
        <h2 className="font-semibold text-gray-800">
          Entries ({challenge.submissions.length})
        </h2>
        <SubmissionList submissions={challenge.submissions} />
      </section>
    </main>
  );
}
