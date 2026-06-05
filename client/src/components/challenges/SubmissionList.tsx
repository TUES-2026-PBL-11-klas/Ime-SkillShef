import type { Submission } from "@/schemas/challenges";

export function SubmissionList({ submissions }: { submissions: Submission[] }) {
  if (submissions.length === 0) {
    return <p className="text-sm text-gray-400">No entries yet — be the first to submit!</p>;
  }

  return (
    <ul className="grid grid-cols-2 gap-4 sm:grid-cols-3">
      {submissions.map((submission) => (
        <li key={submission.id} className="overflow-hidden rounded-lg border border-gray-200 bg-white">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img
            src={submission.mediaUrl}
            alt="Challenge submission"
            className="aspect-square w-full object-cover"
          />
          <div className="p-2 text-xs">
            {submission.score != null && (
              <p className="font-medium text-gray-800">Score: {submission.score}</p>
            )}
            {submission.aiFeedback && (
              <p className="mt-1 line-clamp-3 text-gray-500">{submission.aiFeedback}</p>
            )}
            <time className="mt-1 block text-gray-400">
              {new Date(submission.createdAt).toLocaleDateString()}
            </time>
          </div>
        </li>
      ))}
    </ul>
  );
}
