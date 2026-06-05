import { notFound } from 'next/navigation';
import { getPostAction } from '@/actions/post.actions';
import { getCommentsAction } from '@/actions/engagement.actions';
import { LikeButton } from '@/components/posts/LikeButton';
import { CommentSection } from '@/components/posts/CommentSection';
import Link from 'next/link';

interface Props {
  params: Promise<{ id: string }>;
}

export async function generateMetadata({ params }: Props) {
  const { id } = await params;
  const res = await getPostAction(id);
  return { title: res.success ? `${res.data.title} — SkillChef` : 'Recipe — SkillChef' };
}

export default async function PostDetailPage({ params }: Props) {
  const { id } = await params;

  const [postRes, commentsRes] = await Promise.all([
    getPostAction(id),
    getCommentsAction(id, 0, 20),
  ]);

  if (!postRes.success) notFound();

  const post = postRes.data;
  const comments = commentsRes.success ? commentsRes.data.content : [];

  return (
    <main className="max-w-2xl mx-auto px-4 py-8 space-y-8">
      <Link href="/feed" className="text-sm text-orange-500 hover:text-orange-700">
        ← Back to feed
      </Link>

      {post.imageUrl && (
        <img
          src={post.imageUrl}
          alt={post.title}
          className="w-full rounded-xl object-cover max-h-96"
        />
      )}

      <div className="space-y-2">
        <h1 className="text-2xl font-bold text-gray-900">{post.title}</h1>
        {post.description && (
          <p className="text-gray-600 whitespace-pre-wrap">{post.description}</p>
        )}
        <time className="text-sm text-gray-400 block">
          {new Date(post.createdAt).toLocaleDateString('en-GB', {
            day: 'numeric', month: 'long', year: 'numeric',
          })}
        </time>
      </div>

      <LikeButton postId={post.id} initialCount={post.likeCount} />

      <hr className="border-gray-200" />

      <CommentSection postId={post.id} initialComments={comments} />
    </main>
  );
}
