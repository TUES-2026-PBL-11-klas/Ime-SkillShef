import Link from 'next/link';
import type { PostDetail } from '@/schemas/community';

interface RecipeCardProps {
  post: PostDetail;
}

export function RecipeCard({ post }: RecipeCardProps) {
  return (
    <Link href={`/posts/${post.id}`} className="block group">
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:border-orange-400 transition-colors">
        {post.imageUrl && (
          <img
            src={post.imageUrl}
            alt={post.title}
            className="w-full h-48 object-cover"
          />
        )}
        <div className="p-4">
          <h3 className="font-semibold text-gray-900 group-hover:text-orange-600 transition-colors line-clamp-2">
            {post.title}
          </h3>
          {post.description && (
            <p className="mt-1 text-sm text-gray-500 line-clamp-2">{post.description}</p>
          )}
          <div className="mt-3 flex items-center gap-4 text-sm text-gray-400">
            <span>♥ {post.likeCount}</span>
            <span>💬 {post.commentCount}</span>
            <span className="ml-auto">
              {new Date(post.createdAt).toLocaleDateString()}
            </span>
          </div>
        </div>
      </div>
    </Link>
  );
}
