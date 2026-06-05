-- Sample challenges so the Challenges list is demoable out of the box.
-- Dates are relative to apply time, so the seeded challenges start out "active".
-- Safe to remove once real challenges are authored.

INSERT INTO challenges (title, type, xp_reward, start_date, end_date) VALUES
    ('Perfect Knife Cuts',        'skill',    50,  now() - interval '1 day', now() + interval '6 days'),
    ('One-Pan Weeknight Dinner',  'recipe',   75,  now() - interval '2 days', now() + interval '5 days'),
    ('Plate It Like a Pro',       'creative', 100, now() - interval '1 day', now() + interval '13 days');
