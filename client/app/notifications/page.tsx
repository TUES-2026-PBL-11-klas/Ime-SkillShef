import { NotificationsPanel } from '@/components/notifications/NotificationsPanel';

export const metadata = { title: 'Notifications — SkillChef' };

export default function NotificationsPage() {
  return (
    <main className="max-w-xl mx-auto px-4 py-8">
      <NotificationsPanel />
    </main>
  );
}
