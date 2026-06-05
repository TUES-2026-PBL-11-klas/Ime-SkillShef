import { RecipeCreateForm } from '@/components/posts/RecipeCreateForm';

export const metadata = { title: 'Share a Recipe — SkillChef' };

export default function NewPostPage() {
  return (
    <main className="max-w-xl mx-auto px-4 py-10">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Share a recipe</h1>
      <RecipeCreateForm />
    </main>
  );
}
