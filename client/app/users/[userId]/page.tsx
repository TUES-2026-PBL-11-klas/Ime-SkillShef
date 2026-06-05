import { PublicProfileView } from '@/components/profile/PublicProfileView';

export const metadata = { title: 'Profile — SkillChef' };

interface Props {
  params: Promise<{ userId: string }>;
}

export default async function UserProfilePage({ params }: Props) {
  const { userId } = await params;

  return (
    <main className="max-w-2xl mx-auto px-4 py-8 space-y-6">
      <PublicProfileView userId={userId} />
    </main>
  );
}
