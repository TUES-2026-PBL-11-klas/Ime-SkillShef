'use server';

import * as notificationService from '@/services/notification.service';
import type { ApiResponse } from '@/schemas/api';
import type { NotificationPage } from '@/schemas/community';

export async function getNotificationsAction(page = 0, size = 20): Promise<ApiResponse<NotificationPage>> {
  return notificationService.getNotifications(page, size);
}

export async function getUnreadCountAction(): Promise<ApiResponse<{ count: number }>> {
  return notificationService.getUnreadCount();
}

export async function markAsReadAction(notificationId: string): Promise<ApiResponse<void>> {
  return notificationService.markAsRead(notificationId);
}

export async function markAllAsReadAction(): Promise<ApiResponse<void>> {
  return notificationService.markAllAsRead();
}
