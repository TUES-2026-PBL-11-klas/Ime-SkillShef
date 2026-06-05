import {
  getNotificationsAction,
  getUnreadCountAction,
  markAsReadAction,
  markAllAsReadAction,
} from '@/actions/notification.actions';
import type { ApiResponse } from '@/schemas/api';
import type { NotificationPage } from '@/schemas/community';

export function getNotifications(page: number, size: number): Promise<ApiResponse<NotificationPage>> {
  return getNotificationsAction(page, size);
}

export function getUnreadCount(): Promise<ApiResponse<{ count: number }>> {
  return getUnreadCountAction();
}

export function markAsRead(notificationId: string): Promise<ApiResponse<void>> {
  return markAsReadAction(notificationId);
}

export function markAllAsRead(): Promise<ApiResponse<void>> {
  return markAllAsReadAction();
}
