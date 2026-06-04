'use client';

import { useState } from 'react';
import { useCreatePost } from '@/client/state/useCreatePost';

export function RecipeCreateForm() {
  const { status, error, submit } = useCreatePost();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [imageUrl, setImageUrl] = useState('');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    submit({ title, description: description || undefined, imageUrl: imageUrl || undefined });
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Title <span className="text-red-500">*</span>
        </label>
        <input
          value={title}
          onChange={e => setTitle(e.target.value)}
          required
          maxLength={255}
          placeholder="Crispy garlic butter salmon…"
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-orange-400"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
        <textarea
          value={description}
          onChange={e => setDescription(e.target.value)}
          rows={4}
          placeholder="Share your recipe, tips, or story…"
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-orange-400 resize-none"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
        <input
          value={imageUrl}
          onChange={e => setImageUrl(e.target.value)}
          maxLength={512}
          placeholder="Paste a CDN URL after uploading via the storage layer"
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-orange-400"
        />
        <p className="text-xs text-gray-400 mt-1">
          Use <code className="bg-gray-100 px-1 rounded">POST /api/storage/presign</code> with{' '}
          <code className="bg-gray-100 px-1 rounded">uploadType: RECIPE_IMAGE</code> to get a signed upload URL.
        </p>
      </div>

      {error && <p className="text-sm text-red-500">{error}</p>}

      <button
        type="submit"
        disabled={status === 'submitting' || !title.trim()}
        className="w-full py-2.5 bg-orange-500 text-white font-medium rounded-lg hover:bg-orange-600 disabled:opacity-50 transition-colors"
      >
        {status === 'submitting' ? 'Publishing…' : 'Publish recipe'}
      </button>
    </form>
  );
}
