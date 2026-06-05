'use client';

import { useState } from 'react';
import { useComments } from '@/client/state/useEngagement';
import type { Comment } from '@/schemas/community';

interface CommentSectionProps {
  postId: string;
  initialComments: Comment[];
}

export function CommentSection({ postId, initialComments }: CommentSectionProps) {
  const { comments, status, error, submit, remove } = useComments(postId, initialComments);
  const [text, setText] = useState('');

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!text.trim()) return;
    await submit(text.trim());
    setText('');
  }

  return (
    <div className="space-y-4">
      <h3 className="font-semibold text-gray-800">Comments ({comments.length})</h3>

      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Add a comment…"
          maxLength={2000}
          className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-orange-400"
        />
        <button
          type="submit"
          disabled={status === 'submitting' || !text.trim()}
          className="px-4 py-2 bg-orange-500 text-white text-sm rounded-lg hover:bg-orange-600 disabled:opacity-50 transition-colors"
        >
          Post
        </button>
      </form>

      {error && <p className="text-sm text-red-500">{error}</p>}

      <ul className="space-y-3">
        {comments.map(comment => (
          <li key={comment.id} className="bg-gray-50 rounded-lg px-4 py-3 text-sm">
            <div className="flex items-start justify-between gap-2">
              <p className="text-gray-800">{comment.content}</p>
              <button
                onClick={() => remove(comment.id)}
                className="text-gray-400 hover:text-red-500 shrink-0 text-xs"
                aria-label="Delete comment"
              >
                ✕
              </button>
            </div>
            <time className="text-xs text-gray-400 mt-1 block">
              {new Date(comment.createdAt).toLocaleString()}
            </time>
          </li>
        ))}
      </ul>
    </div>
  );
}
