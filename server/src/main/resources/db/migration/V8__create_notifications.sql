-- In-app notification model.
-- Triggers: LIKE (actor liked a post), COMMENT (actor commented on a post),
--           FOLLOW (actor started following the user).
-- entity_id references the post or comment that triggered the event (null for FOLLOW).

CREATE TABLE notifications (
    id          uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     uuid         NOT NULL,    -- recipient
    actor_id    uuid         NOT NULL,    -- who triggered the event
    type        varchar(50)  NOT NULL,    -- LIKE | COMMENT | FOLLOW
    entity_id   uuid,                    -- recipe_post id or comment id (nullable for FOLLOW)
    is_read     boolean      NOT NULL DEFAULT false,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)  REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_actor
        FOREIGN KEY (actor_id) REFERENCES users (id) ON DELETE CASCADE
);

-- User's notification inbox, newest first.
CREATE INDEX idx_notifications_user_id ON notifications (user_id, created_at DESC);
-- Fast unread badge count.
CREATE INDEX idx_notifications_unread  ON notifications (user_id, is_read) WHERE NOT is_read;
