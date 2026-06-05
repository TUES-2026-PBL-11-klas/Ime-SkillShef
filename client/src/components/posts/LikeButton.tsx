'use client';

import { useLike } from '@/client/state/useEngagement';

interface LikeButtonProps {
  postId: string;
  initialCount: number;
}

export function LikeButton({ postId, initialCount }: LikeButtonProps) {
  const { likeCount, liked, toggle, pending } = useLike(postId, initialCount);

  return (
    <button
      onClick={toggle}
      disabled={pending}
      className={`flex items-center gap-1.5 px-4 py-2 rounded-full border transition-colors text-sm font-medium
        ${liked
          ? 'bg-red-50 border-red-300 text-red-600'
          : 'bg-white border-gray-300 text-gray-600 hover:border-red-300 hover:text-red-500'
        } disabled:opacity-50`}
    >
      <span>{liked ? '♥' : '♡'}</span>
      <span>{likeCount}</span>
    </button>
  );
}
