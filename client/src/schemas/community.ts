import z from 'zod';

// --- Posts ---

export const PostSchema = z.object({
  id: z.string().uuid(),
  userId: z.string().uuid(),
  title: z.string(),
  description: z.string().nullable(),
  imageUrl: z.string().nullable(),
  createdAt: z.string(),
});

export const PostDetailSchema = PostSchema.extend({
  likeCount: z.number(),
  commentCount: z.number(),
});

export const PostPageSchema = z.object({
  content: z.array(PostDetailSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});

export const CreatePostSchema = z.object({
  title: z.string().min(1, 'Title is required').max(255),
  description: z.string().optional(),
  imageUrl: z.string().max(512).optional(),
});

export const UpdatePostSchema = CreatePostSchema;

// --- Comments ---

export const CommentSchema = z.object({
  id: z.string().uuid(),
  userId: z.string().uuid(),
  recipePostId: z.string().uuid(),
  content: z.string(),
  createdAt: z.string(),
});

export const CommentPageSchema = z.object({
  content: z.array(CommentSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});

export const AddCommentSchema = z.object({
  content: z.string().min(1, 'Comment cannot be empty').max(2000),
});

// --- Engagement ---

export const EngagementSummarySchema = z.object({
  likeCount: z.number(),
  commentCount: z.number(),
});

// --- Notifications ---

export const NotificationTypeSchema = z.enum(['LIKE', 'COMMENT', 'FOLLOW']);

export const NotificationSchema = z.object({
  id: z.string().uuid(),
  actorId: z.string().uuid(),
  type: NotificationTypeSchema,
  entityId: z.string().uuid().nullable(),
  read: z.boolean(),
  createdAt: z.string(),
});

export const NotificationPageSchema = z.object({
  content: z.array(NotificationSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
});

export const UnreadCountSchema = z.object({ count: z.number() });

// --- Inferred types ---

export type Post = z.infer<typeof PostSchema>;
export type PostDetail = z.infer<typeof PostDetailSchema>;
export type PostPage = z.infer<typeof PostPageSchema>;
export type CreatePostInput = z.infer<typeof CreatePostSchema>;
export type Comment = z.infer<typeof CommentSchema>;
export type CommentPage = z.infer<typeof CommentPageSchema>;
export type AddCommentInput = z.infer<typeof AddCommentSchema>;
export type EngagementSummary = z.infer<typeof EngagementSummarySchema>;
export type Notification = z.infer<typeof NotificationSchema>;
export type NotificationPage = z.infer<typeof NotificationPageSchema>;
