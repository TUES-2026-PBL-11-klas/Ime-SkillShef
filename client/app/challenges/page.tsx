import { listChallenges } from "@/services/challenges";
import { ChallengeCard } from "@/components/challenges/ChallengeCard";

export const metadata = { title: "Challenges — SkillChef" };
export const dynamic = "force-dynamic";

export default async function ChallengesPage() {
  const res = await listChallenges();
  const challenges = res.success ? res.data : [];

  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Challenges</h1>
        <p className="text-sm text-gray-500">
          Practice a technique, cook the dish, and submit your result.
        </p>
      </div>

      {challenges.length === 0 ? (
        <p className="text-gray-500">No challenges available right now. Check back soon!</p>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {challenges.map((challenge) => (
            <ChallengeCard key={challenge.id} challenge={challenge} />
          ))}
        </div>
      )}
    </main>
  );
}
