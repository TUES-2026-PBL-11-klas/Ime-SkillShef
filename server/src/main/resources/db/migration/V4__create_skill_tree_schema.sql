-- Skill Tree & Learning Content: SKILL_DOMAINS, SKILL_NODES, LESSONS, QUIZZES,
-- SKILL_PROGRESS, XP_TRANSACTIONS.
-- Follows existing conventions: uuid PKs, timestamptz created_at, named constraints.

CREATE TABLE skill_domains (
    id          uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        varchar(255) NOT NULL,
    description text,
    created_at  timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT uq_skill_domains_name UNIQUE (name)
);

CREATE TABLE skill_nodes (
    id              uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    domain_id       uuid         NOT NULL,
    parent_node_id  uuid,
    title           varchar(255) NOT NULL,
    description     text,
    tier            integer      NOT NULL DEFAULT 1,
    xp_reward       integer      NOT NULL DEFAULT 0,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_skill_nodes_domain
        FOREIGN KEY (domain_id) REFERENCES skill_domains (id) ON DELETE CASCADE,
    CONSTRAINT fk_skill_nodes_parent
        FOREIGN KEY (parent_node_id) REFERENCES skill_nodes (id) ON DELETE SET NULL
);

CREATE INDEX idx_skill_nodes_domain_id ON skill_nodes (domain_id);
CREATE INDEX idx_skill_nodes_tier ON skill_nodes (tier);
CREATE INDEX idx_skill_nodes_parent_id ON skill_nodes (parent_node_id);

CREATE TABLE lessons (
    id               uuid         PRIMARY KEY DEFAULT gen_random_uuid(),
    node_id          uuid         NOT NULL,
    title            varchar(255) NOT NULL,
    description      text,
    video_url        varchar(512) NOT NULL,
    duration_seconds integer      NOT NULL DEFAULT 0,
    created_at       timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT fk_lessons_node
        FOREIGN KEY (node_id) REFERENCES skill_nodes (id) ON DELETE CASCADE
);

CREATE INDEX idx_lessons_node_id ON lessons (node_id);

CREATE TABLE quizzes (
    id             uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    node_id        uuid        NOT NULL,
    question       text        NOT NULL,
    options        jsonb       NOT NULL,
    correct_answer varchar(255) NOT NULL,
    created_at     timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_quizzes_node
        FOREIGN KEY (node_id) REFERENCES skill_nodes (id) ON DELETE CASCADE
);

CREATE INDEX idx_quizzes_node_id ON quizzes (node_id);

CREATE TABLE skill_progress (
    user_id          uuid        NOT NULL,
    node_id          uuid        NOT NULL,
    progress_percent integer     NOT NULL DEFAULT 0,
    unlocked_at      timestamptz,
    completed_at     timestamptz,
    created_at       timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT pk_skill_progress PRIMARY KEY (user_id, node_id),
    CONSTRAINT fk_skill_progress_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_skill_progress_node
        FOREIGN KEY (node_id) REFERENCES skill_nodes (id) ON DELETE CASCADE
);

CREATE INDEX idx_skill_progress_user_id ON skill_progress (user_id);
CREATE INDEX idx_skill_progress_node_id ON skill_progress (node_id);

CREATE TABLE xp_transactions (
    id           uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      uuid        NOT NULL,
    amount       integer     NOT NULL,
    reason       varchar(255) NOT NULL,
    source_type  varchar(50),
    source_id    uuid,
    created_at   timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_xp_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_xp_transactions_user_id ON xp_transactions (user_id, created_at DESC);

-- Seed initial skill tree content
INSERT INTO skill_domains (id, name, description)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Cooking Basics', 'Foundational skills for everyday cooking.'),
    ('22222222-2222-2222-2222-222222222222', 'Baking', 'Core baking techniques and dough handling.');

INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', NULL,
     'Knife Skills', 'Learn safe handling and basic cuts.', 1, 50),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     'Julienne', 'Practice consistent julienne cuts.', 2, 75),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', NULL,
     'Baking Fundamentals', 'Understand doughs, batters, and oven basics.', 1, 60),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
     'Sourdough Starter', 'Maintain and use a basic sourdough starter.', 2, 90);

INSERT INTO lessons (id, node_id, title, description, video_url, duration_seconds)
VALUES
    ('e1111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     'Knife Skills 101', 'Grip, safety, and basic cuts.', 'https://cdn.example.com/lessons/knife-skills-101.mp4', 420),
    ('e2222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     'Mastering Julienne', 'Consistency and speed for julienne cuts.', 'https://cdn.example.com/lessons/julienne.mp4', 360),
    ('e3333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
     'Baking Fundamentals', 'Batter vs. dough, measuring, and temperatures.', 'https://cdn.example.com/lessons/baking-fundamentals.mp4', 540),
    ('e4444444-4444-4444-4444-444444444444', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
     'Sourdough Starter Basics', 'Feeding schedule and hydration ratios.', 'https://cdn.example.com/lessons/sourdough-starter.mp4', 600);

INSERT INTO quizzes (id, node_id, question, options, correct_answer)
VALUES
    ('f1111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     'Which grip gives the most control when chopping?',
     '["Claw grip", "Pinch grip", "Palm grip"]'::jsonb,
     'Pinch grip'),
    ('f2222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     'What does "julienne" refer to?',
     '["Thin matchstick cuts", "Large dice", "Rough chop"]'::jsonb,
     'Thin matchstick cuts'),
    ('f3333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
     'Which is most important for consistent baking?',
     '["Accurate measurements", "High heat", "Large pans"]'::jsonb,
     'Accurate measurements'),
    ('f4444444-4444-4444-4444-444444444444', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
     'A sourdough starter is primarily made of:',
     '["Flour and water", "Yeast and sugar", "Milk and eggs"]'::jsonb,
     'Flour and water');
