'use client';

import type { FeedSort } from '@/client/state/useFeed';

interface FeedSortTabsProps {
  sort: FeedSort;
  onChange: (sort: FeedSort) => void;
}

export function FeedSortTabs({ sort, onChange }: FeedSortTabsProps) {
  return (
    <div className="flex rounded-lg border border-gray-200 p-1 w-fit">
      {(['RECENT', 'TRENDING'] as FeedSort[]).map(s => (
        <button
          key={s}
          onClick={() => onChange(s)}
          className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
            sort === s
              ? 'bg-orange-500 text-white'
              : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          {s === 'RECENT' ? '🏠 Following' : '🔥 Trending'}
        </button>
      ))}
    </div>
  );
}
