import { SettingsPanel } from '@/components/settings/SettingsPanel';

export const metadata = { title: 'Settings — SkillChef' };

export default function SettingsPage() {
  return (
    <main className="max-w-2xl mx-auto px-4 py-8 space-y-6">
      <h1 className="text-2xl font-bold text-foreground">Settings</h1>
      <SettingsPanel />
    </main>
  );
}
