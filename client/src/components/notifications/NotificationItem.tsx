import type { Notification } from '@/schemas/community';

const labels: Record<Notification['type'], string> = {
  LIKE: '♥ Someone liked your recipe',
  COMMENT: '💬 Someone commented on your recipe',
  FOLLOW: '👤 Someone started following you',
};

interface NotificationItemProps {
  notification: Notification;
  onMarkRead: (id: string) => void;
}

export function NotificationItem({ notification, onMarkRead }: NotificationItemProps) {
  return (
    <div
      className={`flex items-start gap-3 px-4 py-3 border-b border-gray-100 last:border-0 ${
        !notification.read ? 'bg-orange-50' : 'bg-white'
      }`}
    >
      <div className="flex-1 min-w-0">
        <p className="text-sm text-gray-800">{labels[notification.type]}</p>
        <time className="text-xs text-gray-400">
          {new Date(notification.createdAt).toLocaleString()}
        </time>
      </div>
      {!notification.read && (
        <button
          onClick={() => onMarkRead(notification.id)}
          className="text-xs text-orange-500 hover:text-orange-700 shrink-0"
        >
          Mark read
        </button>
      )}
    </div>
  );
}
