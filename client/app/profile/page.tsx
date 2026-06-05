import { OwnProfileView } from '@/components/profile/OwnProfileView';

export const metadata = { title: 'My profile — SkillChef' };

export default function ProfilePage() {
  return (
    <main className="max-w-2xl mx-auto px-4 py-8 space-y-6">
      <h1 className="text-2xl font-bold text-foreground">Profile</h1>
      <OwnProfileView />
    </main>
  );
}
