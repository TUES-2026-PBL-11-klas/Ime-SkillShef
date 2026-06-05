'use client';

import { useState } from 'react';
import { useFeed, type FeedSort } from '@/client/state/useFeed';
import { FeedSortTabs } from './FeedSortTabs';
import { RecipeCard } from '@/components/posts/RecipeCard';

export function FeedList() {
  const [sort, setSort] = useState<FeedSort>('TRENDING');
  const { posts, status, error, hasMore, loadMore } = useFeed(sort);

  return (
    <div className="space-y-6">
      <FeedSortTabs sort={sort} onChange={setSort} />

      {error && (
        <p className="text-red-500 text-sm">
          {sort === 'RECENT'
            ? 'Sign in to see your personalised feed.'
            : `Failed to load feed: ${error}`}
        </p>
      )}

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {posts.map(post => (
          <RecipeCard key={post.id} post={post} />
        ))}
      </div>

      {status === 'loading' && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="h-64 bg-gray-100 rounded-xl animate-pulse" />
          ))}
        </div>
      )}

      {posts.length === 0 && status === 'idle' && !error && (
        <p className="text-gray-500 text-center py-12">
          {sort === 'RECENT'
            ? 'Follow some chefs to see their recipes here.'
            : 'No recipes yet — be the first to share one!'}
        </p>
      )}

      {hasMore && status !== 'loading' && (
        <div className="flex justify-center">
          <button
            onClick={loadMore}
            className="px-6 py-2 border border-gray-300 rounded-full text-sm text-gray-600 hover:border-orange-400 hover:text-orange-600 transition-colors"
          >
            Load more
          </button>
        </div>
      )}
    </div>
  );
}
