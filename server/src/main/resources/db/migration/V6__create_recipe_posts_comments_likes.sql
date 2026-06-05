-- Community feed feature: RECIPE_POSTS, COMMENTS, LIKES.
-- Consistent with V1 conventions: uuid PKs via gen_random_uuid(), timestamptz,
-- named FK constraints, ON DELETE CASCADE on owning relationships.
-- Indexes are chosen to support the three main feed query patterns:
--   1. Recency-sorted global/followed feed   (recipe_posts.created_at DESC)
--   2. Per-user post list                    (user_id, created_at DESC)
--   3. Engagement counts per post            (likes/comments by recipe_post_id)

CREATE TABLE recipe_posts (
    id          uuid          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     uuid          NOT NULL,
    title       varchar(255)  NOT NULL,
    description text,
    image_url   varchar(512),
    created_at  timestamptz   NOT NULL DEFAULT now(),
    CONSTRAINT fk_recipe_posts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Global feed sorted by recency.
CREATE INDEX idx_recipe_posts_created_at  ON recipe_posts (created_at DESC);
-- A single user's posts, newest first.
CREATE INDEX idx_recipe_posts_user_id     ON recipe_posts (user_id, created_at DESC);

CREATE TABLE comments (
    id             uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        uuid        NOT NULL,
    recipe_post_id uuid        NOT NULL,
    content        text        NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)         REFERENCES users        (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_recipe_post
        FOREIGN KEY (recipe_post_id)  REFERENCES recipe_posts (id) ON DELETE CASCADE
);

-- Paginate a post's comments chronologically.
CREATE INDEX idx_comments_recipe_post_id  ON comments (recipe_post_id, created_at);
-- A user's comment history.
CREATE INDEX idx_comments_user_id         ON comments (user_id, created_at DESC);

CREATE TABLE likes (
    id             uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        uuid        NOT NULL,
    recipe_post_id uuid        NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_likes_user
        FOREIGN KEY (user_id)         REFERENCES users        (id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_recipe_post
        FOREIGN KEY (recipe_post_id)  REFERENCES recipe_posts (id) ON DELETE CASCADE,
    -- One like per user per post.
    CONSTRAINT uq_likes_user_post UNIQUE (user_id, recipe_post_id)
);

-- Like counts and "has this user liked?" lookups.
CREATE INDEX idx_likes_recipe_post_id     ON likes (recipe_post_id);
