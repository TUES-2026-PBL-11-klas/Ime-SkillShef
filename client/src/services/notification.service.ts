import * as notificationsApi from '@/external/notifications';
import type { NotificationPage } from '@/schemas/community';
import type { ApiResponse } from '@/schemas/api';

export async function getNotifications(page: number, size: number): Promise<ApiResponse<NotificationPage>> {
  return notificationsApi.getNotifications(page, size);
}

export async function getUnreadCount(): Promise<ApiResponse<{ count: number }>> {
  return notificationsApi.getUnreadCount();
}

export async function markAsRead(notificationId: string): Promise<ApiResponse<void>> {
  return notificationsApi.markAsRead(notificationId);
}

export async function markAllAsRead(): Promise<ApiResponse<void>> {
  return notificationsApi.markAllAsRead();
}
