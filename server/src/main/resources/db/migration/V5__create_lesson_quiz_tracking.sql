-- Lesson & Quiz progress tracking for the Lesson/Quiz services (issue #15).
-- Follows existing conventions: uuid PKs, timestamptz created_at, named
-- constraints, ON DELETE CASCADE on owning relationships.

-- Records that a user has watched a given lesson. Composite PK keeps it
-- idempotent: re-watching simply refreshes watched_at via the application.
CREATE TABLE lesson_watches (
    user_id     uuid        NOT NULL,
    lesson_id   uuid        NOT NULL,
    watched_at  timestamptz NOT NULL DEFAULT now(),
    created_at  timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT pk_lesson_watches PRIMARY KEY (user_id, lesson_id),
    CONSTRAINT fk_lesson_watches_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_watches_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE
);

CREATE INDEX idx_lesson_watches_user_id ON lesson_watches (user_id);
CREATE INDEX idx_lesson_watches_lesson_id ON lesson_watches (lesson_id);

-- Every quiz answer submission. Keeps full history; the first correct answer
-- per (user, quiz) is what awards XP (awarded_xp > 0 marks that row).
CREATE TABLE quiz_attempts (
    id              uuid        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         uuid        NOT NULL,
    quiz_id         uuid        NOT NULL,
    selected_answer varchar(255) NOT NULL,
    is_correct      boolean     NOT NULL,
    awarded_xp      integer     NOT NULL DEFAULT 0,
    created_at      timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_quiz_attempts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_attempts_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

-- Per-user attempt history (newest first) and "has this user passed this quiz".
CREATE INDEX idx_quiz_attempts_user_quiz ON quiz_attempts (user_id, quiz_id, created_at DESC);
CREATE INDEX idx_quiz_attempts_quiz_id ON quiz_attempts (quiz_id);
