'use client';

import { useState, useEffect, useCallback } from 'react';
import * as feedActions from '@/client/actions/feed.actions';
import type { PostDetail } from '@/schemas/community';

export type FeedSort = 'RECENT' | 'TRENDING';

export function useFeed(sort: FeedSort) {
  const [posts, setPosts] = useState<PostDetail[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [status, setStatus] = useState<'idle' | 'loading' | 'error'>('idle');
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async (nextPage: number) => {
    setStatus('loading');
    const res = sort === 'RECENT'
      ? await feedActions.getFollowingFeed(nextPage, 20)
      : await feedActions.getTrendingFeed(nextPage, 20);

    if (!res.success) {
      setError(res.error);
      setStatus('error');
      return;
    }
    setPosts(prev => nextPage === 0 ? res.data.content : [...prev, ...res.data.content]);
    setTotalPages(res.data.totalPages);
    setPage(nextPage);
    setStatus('idle');
  }, [sort]);

  useEffect(() => {
    setPosts([]);
    setPage(0);
    load(0);
  }, [sort, load]);

  const loadMore = useCallback(() => {
    if (page + 1 < totalPages && status !== 'loading') load(page + 1);
  }, [page, totalPages, status, load]);

  return { posts, status, error, hasMore: page + 1 < totalPages, loadMore };
}
