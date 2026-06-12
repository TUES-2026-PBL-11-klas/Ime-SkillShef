import { AssistantExperience } from "@/components/assistant/AssistantExperience";
import { listConversations } from "@/services/ai";

export const metadata = { title: "AI Chef Assistant — SkillChef" };
export const dynamic = "force-dynamic";

export default async function AssistantPage() {
  const res = await listConversations();
  const conversations = res.success ? res.data : [];

  return (
    <main className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">AI Chef Assistant</h1>
        <p className="text-sm text-gray-500">
          Your personal culinary coach — ask about techniques, recipes, or substitutions.
        </p>
      </div>
      <AssistantExperience initialConversations={conversations} />
    </main>
  );
}
