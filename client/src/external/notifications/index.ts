import { http } from '@/external/http';
import type { ApiResponse } from '@/schemas/api';
import type { NotificationPage } from '@/schemas/community';

export function getNotifications(page = 0, size = 20): Promise<ApiResponse<NotificationPage>> {
  return http({ method: 'GET', path: `/api/notifications?page=${page}&size=${size}` });
}

export function getUnreadCount(): Promise<ApiResponse<{ count: number }>> {
  return http({ method: 'GET', path: '/api/notifications/unread-count' });
}

export function markAsRead(notificationId: string): Promise<ApiResponse<void>> {
  return http({ method: 'PATCH', path: `/api/notifications/${notificationId}/read` });
}

export function markAllAsRead(): Promise<ApiResponse<void>> {
  return http({ method: 'PATCH', path: '/api/notifications/read-all' });
}
