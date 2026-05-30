-- Challenges & AI Assistant feature: CHALLENGES, SUBMISSIONS,
-- AI_CONVERSATIONS, AI_MESSAGES.
-- Modeled after the SkillChef ER diagram and consistent with V1's conventions
-- (uuid PKs via gen_random_uuid(), timestamptz created_at, named constraints,
-- ON DELETE CASCADE on owning relationships).

CREATE TABLE challenges (
    id          uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    title       varchar(255) NOT NULL,
    type        varchar(50)  NOT NULL,
    xp_reward   integer      NOT NULL DEFAULT 0,
    start_date  timestamptz  NOT NULL,
    end_date    timestamptz  NOT NULL,
    created_at  timestamptz  NOT NULL DEFAULT now()
);

-- Active-challenge listing filters by the start/end window ordered by recency.
CREATE INDEX idx_challenges_active_window ON challenges (start_date, end_date);

CREATE TABLE submissions (
    id           uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    challenge_id uuid         NOT NULL,
    user_id      uuid         NOT NULL,
    media_url    varchar(512) NOT NULL,
    ai_feedback  text,
    score        integer,
    created_at   timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_submissions_challenge
        FOREIGN KEY (challenge_id) REFERENCES challenges (id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Per-challenge submission lists and a per-user history (newest first).
CREATE INDEX idx_submissions_challenge_id ON submissions (challenge_id);
CREATE INDEX idx_submissions_user_challenge ON submissions (user_id, challenge_id, created_at DESC);

CREATE TABLE ai_conversations (
    id          uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     uuid         NOT NULL,
    title       varchar(255),
    created_at  timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_ai_conversations_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Listing a user's conversations, most recent first.
CREATE INDEX idx_ai_conversations_user_id ON ai_conversations (user_id, created_at DESC);

CREATE TABLE ai_messages (
    id              uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id uuid         NOT NULL,
    role            varchar(20)  NOT NULL,
    content         text         NOT NULL,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_ai_messages_conversation
        FOREIGN KEY (conversation_id) REFERENCES ai_conversations (id) ON DELETE CASCADE
);

-- Paginating a conversation's message history in chronological order.
CREATE INDEX idx_ai_messages_conversation ON ai_messages (conversation_id, created_at);
