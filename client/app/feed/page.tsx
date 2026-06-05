import { FeedList } from '@/components/community/FeedList';
import Link from 'next/link';

export const metadata = { title: 'Community Feed — SkillChef' };

export default function FeedPage() {
  return (
    <main className="max-w-5xl mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Community</h1>
        <Link
          href="/posts/new"
          className="px-4 py-2 bg-orange-500 text-white text-sm font-medium rounded-lg hover:bg-orange-600 transition-colors"
        >
          + Share a recipe
        </Link>
      </div>
      <FeedList />
    </main>
  );
}
