'use client';

import { useState, useEffect, useCallback } from 'react';
import * as notifActions from '@/client/actions/notification.actions';
import type { Notification } from '@/schemas/community';

export function useNotifications() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [status, setStatus] = useState<'idle' | 'loading' | 'error'>('idle');

  const load = useCallback(async () => {
    setStatus('loading');
    const [notifRes, countRes] = await Promise.all([
      notifActions.getNotifications(0, 20),
      notifActions.getUnreadCount(),
    ]);
    if (notifRes.success) setNotifications(notifRes.data.content);
    if (countRes.success) setUnreadCount(countRes.data.count);
    setStatus('idle');
  }, []);

  useEffect(() => { load(); }, [load]);

  const markRead = useCallback(async (id: string) => {
    await notifActions.markAsRead(id);
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
    setUnreadCount(prev => Math.max(0, prev - 1));
  }, []);

  const markAllRead = useCallback(async () => {
    await notifActions.markAllAsRead();
    setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    setUnreadCount(0);
  }, []);

  return { notifications, unreadCount, status, markRead, markAllRead };
}
