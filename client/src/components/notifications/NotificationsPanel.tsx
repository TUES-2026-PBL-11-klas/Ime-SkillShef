'use client';

import { useNotifications } from '@/client/state/useNotifications';
import { NotificationItem } from './NotificationItem';

export function NotificationsPanel() {
  const { notifications, unreadCount, status, markRead, markAllRead } = useNotifications();

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
        <h2 className="font-semibold text-gray-900">
          Notifications
          {unreadCount > 0 && (
            <span className="ml-2 bg-orange-500 text-white text-xs font-bold rounded-full px-2 py-0.5">
              {unreadCount}
            </span>
          )}
        </h2>
        {unreadCount > 0 && (
          <button
            onClick={markAllRead}
            className="text-sm text-orange-500 hover:text-orange-700"
          >
            Mark all read
          </button>
        )}
      </div>

      {status === 'loading' && (
        <div className="p-6 text-center text-sm text-gray-400">Loading…</div>
      )}

      {status === 'idle' && notifications.length === 0 && (
        <div className="p-8 text-center text-sm text-gray-400">
          You're all caught up!
        </div>
      )}

      <ul>
        {notifications.map(n => (
          <li key={n.id}>
            <NotificationItem notification={n} onMarkRead={markRead} />
          </li>
        ))}
      </ul>
    </div>
  );
}
