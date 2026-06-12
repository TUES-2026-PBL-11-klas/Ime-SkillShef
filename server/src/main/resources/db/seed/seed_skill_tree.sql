-- ============================================================
-- Skill Tree Seed Data
-- 5 domains, 6 nodes each (2 per tier, 3 tiers), 1 lesson +
-- 1 quiz per node. Safe to re-run (ON CONFLICT DO NOTHING).
--
-- UUID scheme (all valid hex):
--   domains  d0000000-0000-0000-0000-00000000000{1-5}
--   nodes    a{domain}{tier}{seq}0000-0000-0000-0000-000000000000
--   lessons  b{domain}{tier}{seq}0000-0000-0000-0000-000000000000
--   quizzes  c{domain}{tier}{seq}0000-0000-0000-0000-000000000000
-- ============================================================

-- ─── CLEAR EXISTING SKILL TREE DATA ─────────────────────────
-- CASCADE removes dependent nodes, lessons, quizzes, progress, and xp_transactions.

TRUNCATE skill_domains CASCADE;

-- ─── DOMAINS ─────────────────────────────────────────────────

INSERT INTO skill_domains (id, name, description) VALUES
    ('d0000000-0000-0000-0000-000000000001', 'Cooking Basics',      'Foundational skills every cook needs.'),
    ('d0000000-0000-0000-0000-000000000002', 'Baking & Pastry',     'Breads, doughs, and sweet baked goods.'),
    ('d0000000-0000-0000-0000-000000000003', 'Sauces & Stocks',     'Classical and modern sauces from scratch.'),
    ('d0000000-0000-0000-0000-000000000004', 'Meat & Poultry',      'Butchery, marinades, and cooking techniques.'),
    ('d0000000-0000-0000-0000-000000000005', 'Plant-Based Cooking', 'Vegetables, legumes, and meat-free dishes.');

-- ─── SKILL NODES ─────────────────────────────────────────────
-- Tier 1: two root nodes per domain (parent_node_id NULL)
-- Tier 2: each depends on one tier-1 node
-- Tier 3: each depends on one tier-2 node

-- Domain 1 — Cooking Basics
INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward) VALUES
    ('a1110000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001', NULL,
     'Knife Skills',        'Safe grip, board technique, and the five essential cuts.',     1,  50),
    ('a1120000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001', NULL,
     'Heat & Temperature',  'Conduction, convection, and radiant heat explained.',          1,  50),
    ('a1210000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001',
     'a1110000-0000-0000-0000-000000000000',
     'Mise en Place',       'Prep, organisation, and station management.',                  2,  75),
    ('a1220000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001',
     'a1120000-0000-0000-0000-000000000000',
     'Sautéing & Pan Work', 'Hot-pan techniques: sauté, toss, and deglaze.',               2,  75),
    ('a1310000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001',
     'a1210000-0000-0000-0000-000000000000',
     'Seasoning & Tasting', 'Salt timing, acid balance, and building flavour layers.',      3, 100),
    ('a1320000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000001',
     'a1220000-0000-0000-0000-000000000000',
     'Emulsification',      'Stable emulsions: vinaigrettes, pan sauces, and mayo.',        3, 100)
ON CONFLICT DO NOTHING;

-- Domain 2 — Baking & Pastry
INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward) VALUES
    ('a2110000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002', NULL,
     'Baking Science',      'How flour, fat, liquid, and leaveners interact.',              1,  50),
    ('a2120000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002', NULL,
     'Shortcrust Pastry',   'Rubbing fat into flour for tarts and quiches.',                1,  50),
    ('a2210000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002',
     'a2110000-0000-0000-0000-000000000000',
     'Yeast Breads',        'Kneading, proofing, and shaping yeasted doughs.',              2,  75),
    ('a2220000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002',
     'a2120000-0000-0000-0000-000000000000',
     'Choux Pastry',        'Steam-leavened dough for éclairs and profiteroles.',           2,  75),
    ('a2310000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002',
     'a2210000-0000-0000-0000-000000000000',
     'Sourdough',           'Wild-yeast fermentation and open crumb structure.',            3, 110),
    ('a2320000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000002',
     'a2220000-0000-0000-0000-000000000000',
     'Laminated Dough',     'Croissants and puff: butter lock-in, turns, and proofing.',    3, 120)
ON CONFLICT DO NOTHING;

-- Domain 3 — Sauces & Stocks
INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward) VALUES
    ('a3110000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003', NULL,
     'Stocks & Broths',     'Brown, white, and vegetable stocks from bones and aromatics.', 1,  50),
    ('a3120000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003', NULL,
     'Roux & Thickeners',   'Roux, slurries, and reduction as thickening methods.',         1,  50),
    ('a3210000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003',
     'a3110000-0000-0000-0000-000000000000',
     'Mother Sauces',       'Béchamel, velouté, espagnole, hollandaise, and tomato.',       2,  80),
    ('a3220000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003',
     'a3120000-0000-0000-0000-000000000000',
     'Pan Sauces',          'Fond-based sauces: deglazing and mounting with butter.',       2,  75),
    ('a3310000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003',
     'a3210000-0000-0000-0000-000000000000',
     'Derivative Sauces',   'Mornay, bordelaise, and other small sauces.',                  3, 100),
    ('a3320000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000003',
     'a3220000-0000-0000-0000-000000000000',
     'Compound Butters',    'Herb, citrus, and flavoured butters to finish dishes.',        3,  90)
ON CONFLICT DO NOTHING;

-- Domain 4 — Meat & Poultry
INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward) VALUES
    ('a4110000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004', NULL,
     'Cuts of Meat',        'Primal and sub-primal cuts: beef, pork, and lamb.',            1,  50),
    ('a4120000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004', NULL,
     'Marinades & Brines',  'Wet and dry brines, acid marinades, and rub blends.',          1,  50),
    ('a4210000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004',
     'a4110000-0000-0000-0000-000000000000',
     'Roasting & Resting',  'High-heat roasting, probe placement, and carry-over cook.',    2,  80),
    ('a4220000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004',
     'a4120000-0000-0000-0000-000000000000',
     'Grilling & Searing',  'Maillard reaction, crust development, and grill marks.',       2,  75),
    ('a4310000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004',
     'a4210000-0000-0000-0000-000000000000',
     'Braising & Slow-Cook','Collagen breakdown, braising liquid ratios, and timing.',       3, 105),
    ('a4320000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000004',
     'a4220000-0000-0000-0000-000000000000',
     'Spatchcock & Truss',  'Breaking down whole birds for even cooking.',                  3, 100)
ON CONFLICT DO NOTHING;

-- Domain 5 — Plant-Based Cooking
INSERT INTO skill_nodes (id, domain_id, parent_node_id, title, description, tier, xp_reward) VALUES
    ('a5110000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005', NULL,
     'Vegetable Prep',      'Peeling, blanching, and preserving colour and texture.',       1,  50),
    ('a5120000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005', NULL,
     'Legumes & Grains',    'Soaking, cooking ratios, and flavour-building with pulses.',   1,  50),
    ('a5210000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005',
     'a5110000-0000-0000-0000-000000000000',
     'Roasting Vegetables', 'Caramelisation, tray spacing, and seasoning timing.',          2,  75),
    ('a5220000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005',
     'a5120000-0000-0000-0000-000000000000',
     'Plant Proteins',      'Tofu, tempeh, and seitan: pressing, marinating, cooking.',     2,  75),
    ('a5310000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005',
     'a5210000-0000-0000-0000-000000000000',
     'Fermentation Basics', 'Lacto-fermented vegetables: kimchi, sauerkraut, pickles.',     3, 100),
    ('a5320000-0000-0000-0000-000000000000', 'd0000000-0000-0000-0000-000000000005',
     'a5220000-0000-0000-0000-000000000000',
     'Umami Without Meat',  'Mushrooms, miso, soy, and nutritional yeast as boosters.',     3, 100)
ON CONFLICT DO NOTHING;

-- ─── LESSONS (one per node) ───────────────────────────────────

INSERT INTO lessons (id, node_id, title, description, video_url, duration_seconds) VALUES
    ('b1110000-0000-0000-0000-000000000000', 'a1110000-0000-0000-0000-000000000000',
     'Knife Skills 101',        'Grip, posture, and the claw technique.',                          'https://cdn.example.com/lessons/knife-skills.mp4',       420),
    ('b1120000-0000-0000-0000-000000000000', 'a1120000-0000-0000-0000-000000000000',
     'Heat Fundamentals',       'How heat transfers and why it matters.',                          'https://cdn.example.com/lessons/heat-fundamentals.mp4',  380),
    ('b1210000-0000-0000-0000-000000000000', 'a1210000-0000-0000-0000-000000000000',
     'Mise en Place Mastery',   'Setting up your station like a professional.',                    'https://cdn.example.com/lessons/mise-en-place.mp4',      360),
    ('b1220000-0000-0000-0000-000000000000', 'a1220000-0000-0000-0000-000000000000',
     'Sauté Technique',         'Pan temperature, fat selection, and the toss.',                   'https://cdn.example.com/lessons/saute.mp4',              400),
    ('b1310000-0000-0000-0000-000000000000', 'a1310000-0000-0000-0000-000000000000',
     'Seasoning Like a Chef',   'When and how to salt throughout the cooking process.',            'https://cdn.example.com/lessons/seasoning.mp4',          300),
    ('b1320000-0000-0000-0000-000000000000', 'a1320000-0000-0000-0000-000000000000',
     'Emulsification Deep Dive','Lecithin, agitation, and fixing broken sauces.',                  'https://cdn.example.com/lessons/emulsification.mp4',    450),
    ('b2110000-0000-0000-0000-000000000000', 'a2110000-0000-0000-0000-000000000000',
     'Baking Science 101',      'Gluten, starch gelatinisation, and Maillard in baking.',         'https://cdn.example.com/lessons/baking-science.mp4',     500),
    ('b2120000-0000-0000-0000-000000000000', 'a2120000-0000-0000-0000-000000000000',
     'Perfect Shortcrust',      'Fat-to-flour ratios and blind baking.',                           'https://cdn.example.com/lessons/shortcrust.mp4',        420),
    ('b2210000-0000-0000-0000-000000000000', 'a2210000-0000-0000-0000-000000000000',
     'Yeast Bread Basics',      'Windowpane test, bulk fermentation, and shaping.',                'https://cdn.example.com/lessons/yeast-breads.mp4',      560),
    ('b2220000-0000-0000-0000-000000000000', 'a2220000-0000-0000-0000-000000000000',
     'Choux from Scratch',      'Panade, egg incorporation, and piping.',                          'https://cdn.example.com/lessons/choux.mp4',             480),
    ('b2310000-0000-0000-0000-000000000000', 'a2310000-0000-0000-0000-000000000000',
     'Sourdough Masterclass',   'Levain building, scoring, and steam baking.',                     'https://cdn.example.com/lessons/sourdough.mp4',         720),
    ('b2320000-0000-0000-0000-000000000000', 'a2320000-0000-0000-0000-000000000000',
     'Croissant Workshop',      'Lock-in butter, turns, and proofing laminated dough.',            'https://cdn.example.com/lessons/laminated-dough.mp4',   660),
    ('b3110000-0000-0000-0000-000000000000', 'a3110000-0000-0000-0000-000000000000',
     'Stocks from Bones',       'Roasting bones, skimming, and straining.',                        'https://cdn.example.com/lessons/stocks.mp4',            600),
    ('b3120000-0000-0000-0000-000000000000', 'a3120000-0000-0000-0000-000000000000',
     'Roux Ratios',             'White, blonde, and brown roux: colour vs. thickening power.',     'https://cdn.example.com/lessons/roux.mp4',              360),
    ('b3210000-0000-0000-0000-000000000000', 'a3210000-0000-0000-0000-000000000000',
     'The Five Mother Sauces',  'Origins, ratios, and uses for each classical sauce.',             'https://cdn.example.com/lessons/mother-sauces.mp4',     540),
    ('b3220000-0000-0000-0000-000000000000', 'a3220000-0000-0000-0000-000000000000',
     'Pan Sauce Fundamentals',  'Fond, deglazing, and mounting with cold butter.',                 'https://cdn.example.com/lessons/pan-sauces.mp4',        380),
    ('b3310000-0000-0000-0000-000000000000', 'a3310000-0000-0000-0000-000000000000',
     'Derivative Sauces',       'From mother sauce to finished small sauce in minutes.',           'https://cdn.example.com/lessons/derivative-sauces.mp4', 440),
    ('b3320000-0000-0000-0000-000000000000', 'a3320000-0000-0000-0000-000000000000',
     'Compound Butters',        'Maître d''hôtel, café de Paris, and anchovy butter.',             'https://cdn.example.com/lessons/compound-butters.mp4',  300),
    ('b4110000-0000-0000-0000-000000000000', 'a4110000-0000-0000-0000-000000000000',
     'Meat Cuts Explained',     'Primal vs. sub-primal and how muscle use affects texture.',       'https://cdn.example.com/lessons/meat-cuts.mp4',         480),
    ('b4120000-0000-0000-0000-000000000000', 'a4120000-0000-0000-0000-000000000000',
     'Brines & Marinades',      'Wet brine science and acid-based marinades.',                     'https://cdn.example.com/lessons/brines-marinades.mp4',  420),
    ('b4210000-0000-0000-0000-000000000000', 'a4210000-0000-0000-0000-000000000000',
     'Perfect Roast',           'Trussing, probe placement, and resting times.',                   'https://cdn.example.com/lessons/roasting.mp4',          500),
    ('b4220000-0000-0000-0000-000000000000', 'a4220000-0000-0000-0000-000000000000',
     'Sear & Grill Mastery',    'Two-zone fire, crust formation, and grill marks.',                'https://cdn.example.com/lessons/grilling.mp4',          460),
    ('b4310000-0000-0000-0000-000000000000', 'a4310000-0000-0000-0000-000000000000',
     'Low & Slow Braising',     'Mirepoix, acid, and collagen-rich cuts.',                         'https://cdn.example.com/lessons/braising.mp4',          560),
    ('b4320000-0000-0000-0000-000000000000', 'a4320000-0000-0000-0000-000000000000',
     'Spatchcock Chicken',      'Removing the backbone for faster, even roasting.',                'https://cdn.example.com/lessons/spatchcock.mp4',        400),
    ('b5110000-0000-0000-0000-000000000000', 'a5110000-0000-0000-0000-000000000000',
     'Vegetable Prep Guide',    'Blanch-shock, mandoline safety, and storage.',                    'https://cdn.example.com/lessons/veg-prep.mp4',          360),
    ('b5120000-0000-0000-0000-000000000000', 'a5120000-0000-0000-0000-000000000000',
     'Legumes & Grains 101',    'Soaking overnight vs. quick-soak and seasoning.',                 'https://cdn.example.com/lessons/legumes-grains.mp4',    400),
    ('b5210000-0000-0000-0000-000000000000', 'a5210000-0000-0000-0000-000000000000',
     'Sheet-Pan Roasting',      'Tray spacing, convection, and caramelisation.',                   'https://cdn.example.com/lessons/roasting-veg.mp4',      340),
    ('b5220000-0000-0000-0000-000000000000', 'a5220000-0000-0000-0000-000000000000',
     'Cooking Plant Proteins',  'Pressing tofu, marinating tempeh, and seitan texture.',           'https://cdn.example.com/lessons/plant-proteins.mp4',    460),
    ('b5310000-0000-0000-0000-000000000000', 'a5310000-0000-0000-0000-000000000000',
     'Fermentation at Home',    'Salt ratios, anaerobic jars, and troubleshooting.',               'https://cdn.example.com/lessons/fermentation.mp4',      520),
    ('b5320000-0000-0000-0000-000000000000', 'a5320000-0000-0000-0000-000000000000',
     'Building Umami',          'Stacking glutamates: mushroom stock, miso glaze, nori.',          'https://cdn.example.com/lessons/umami.mp4',             440)
ON CONFLICT DO NOTHING;

-- ─── QUIZZES (one per node) ──────────────────────────────────

INSERT INTO quizzes (id, node_id, question, options, correct_answer) VALUES
    ('c1110000-0000-0000-0000-000000000000', 'a1110000-0000-0000-0000-000000000000',
     'Which grip gives the most control when chopping?',
     '["Pinch grip", "Palm grip", "Claw grip"]'::jsonb, 'Pinch grip'),

    ('c1120000-0000-0000-0000-000000000000', 'a1120000-0000-0000-0000-000000000000',
     'Which heat-transfer method moves heat through a liquid or gas?',
     '["Conduction", "Convection", "Radiation"]'::jsonb, 'Convection'),

    ('c1210000-0000-0000-0000-000000000000', 'a1210000-0000-0000-0000-000000000000',
     'What does "mise en place" translate to?',
     '["Everything in its place", "High heat", "Season to taste"]'::jsonb, 'Everything in its place'),

    ('c1220000-0000-0000-0000-000000000000', 'a1220000-0000-0000-0000-000000000000',
     'Why should the pan be hot before adding fat when sautéing?',
     '["Prevents sticking", "Adds flavour", "Speeds up browning"]'::jsonb, 'Prevents sticking'),

    ('c1310000-0000-0000-0000-000000000000', 'a1310000-0000-0000-0000-000000000000',
     'Which element best balances excessive richness in a dish?',
     '["Acid", "Salt", "Fat"]'::jsonb, 'Acid'),

    ('c1320000-0000-0000-0000-000000000000', 'a1320000-0000-0000-0000-000000000000',
     'What is the key emulsifier found in egg yolk?',
     '["Lecithin", "Casein", "Gluten"]'::jsonb, 'Lecithin'),

    ('c2110000-0000-0000-0000-000000000000', 'a2110000-0000-0000-0000-000000000000',
     'What protein in flour provides structure in baked goods?',
     '["Gluten", "Casein", "Albumin"]'::jsonb, 'Gluten'),

    ('c2120000-0000-0000-0000-000000000000', 'a2120000-0000-0000-0000-000000000000',
     'What is the purpose of blind-baking a pastry shell?',
     '["Cook the base before adding wet filling", "Add colour", "Create layers"]'::jsonb,
     'Cook the base before adding wet filling'),

    ('c2210000-0000-0000-0000-000000000000', 'a2210000-0000-0000-0000-000000000000',
     'What does the windowpane test check for?',
     '["Gluten development", "Yeast activity", "Dough hydration"]'::jsonb, 'Gluten development'),

    ('c2220000-0000-0000-0000-000000000000', 'a2220000-0000-0000-0000-000000000000',
     'What leavens choux pastry?',
     '["Steam", "Yeast", "Baking powder"]'::jsonb, 'Steam'),

    ('c2310000-0000-0000-0000-000000000000', 'a2310000-0000-0000-0000-000000000000',
     'What is the purpose of scoring sourdough before baking?',
     '["Controls oven spring", "Adds flavour", "Seals in moisture"]'::jsonb, 'Controls oven spring'),

    ('c2320000-0000-0000-0000-000000000000', 'a2320000-0000-0000-0000-000000000000',
     'How many folds (turns) does classic croissant dough typically receive?',
     '["3", "6", "9"]'::jsonb, '6'),

    ('c3110000-0000-0000-0000-000000000000', 'a3110000-0000-0000-0000-000000000000',
     'Why are bones roasted before making a brown stock?',
     '["Develop deeper colour and flavour", "Remove fat", "Speed up extraction"]'::jsonb,
     'Develop deeper colour and flavour'),

    ('c3120000-0000-0000-0000-000000000000', 'a3120000-0000-0000-0000-000000000000',
     'What is the ratio of fat to flour in a standard roux?',
     '["1:1 by weight", "2:1 by weight", "1:2 by weight"]'::jsonb, '1:1 by weight'),

    ('c3210000-0000-0000-0000-000000000000', 'a3210000-0000-0000-0000-000000000000',
     'Which of the five mother sauces uses a tomato base?',
     '["Sauce tomat", "Espagnole", "Velouté"]'::jsonb, 'Sauce tomat'),

    ('c3220000-0000-0000-0000-000000000000', 'a3220000-0000-0000-0000-000000000000',
     'What is the brown crust left in the pan after searing called?',
     '["Fond", "Roux", "Liaison"]'::jsonb, 'Fond'),

    ('c3310000-0000-0000-0000-000000000000', 'a3310000-0000-0000-0000-000000000000',
     'Sauce Mornay is a derivative of which mother sauce?',
     '["Béchamel", "Velouté", "Espagnole"]'::jsonb, 'Béchamel'),

    ('c3320000-0000-0000-0000-000000000000', 'a3320000-0000-0000-0000-000000000000',
     'What is the classic compound butter served with steak?',
     '["Maître d''hôtel butter", "Beurre blanc", "Beurre manié"]'::jsonb, 'Maître d''hôtel butter'),

    ('c4110000-0000-0000-0000-000000000000', 'a4110000-0000-0000-0000-000000000000',
     'Which beef primal is least worked and therefore most tender?',
     '["Tenderloin", "Chuck", "Brisket"]'::jsonb, 'Tenderloin'),

    ('c4120000-0000-0000-0000-000000000000', 'a4120000-0000-0000-0000-000000000000',
     'What does brining meat primarily achieve?',
     '["Retains moisture during cooking", "Adds colour", "Breaks down collagen"]'::jsonb,
     'Retains moisture during cooking'),

    ('c4210000-0000-0000-0000-000000000000', 'a4210000-0000-0000-0000-000000000000',
     'What is carry-over cooking?',
     '["Heat rising in meat after removal from the oven", "Basting during roasting", "Resting in liquid"]'::jsonb,
     'Heat rising in meat after removal from the oven'),

    ('c4220000-0000-0000-0000-000000000000', 'a4220000-0000-0000-0000-000000000000',
     'What reaction produces the brown crust when searing meat?',
     '["Maillard reaction", "Caramelisation", "Oxidation"]'::jsonb, 'Maillard reaction'),

    ('c4310000-0000-0000-0000-000000000000', 'a4310000-0000-0000-0000-000000000000',
     'Which protein breaks down into gelatin during slow braising?',
     '["Collagen", "Myosin", "Albumin"]'::jsonb, 'Collagen'),

    ('c4320000-0000-0000-0000-000000000000', 'a4320000-0000-0000-0000-000000000000',
     'What is removed when spatchcocking a bird?',
     '["The backbone", "The breastbone", "The wing tips"]'::jsonb, 'The backbone'),

    ('c5110000-0000-0000-0000-000000000000', 'a5110000-0000-0000-0000-000000000000',
     'What does blanching and shocking vegetables achieve?',
     '["Sets colour and stops cooking", "Adds flavour", "Removes bitterness"]'::jsonb,
     'Sets colour and stops cooking'),

    ('c5120000-0000-0000-0000-000000000000', 'a5120000-0000-0000-0000-000000000000',
     'Why should dried legumes be soaked before cooking?',
     '["Reduce cooking time and improve digestibility", "Add flavour", "Increase protein"]'::jsonb,
     'Reduce cooking time and improve digestibility'),

    ('c5210000-0000-0000-0000-000000000000', 'a5210000-0000-0000-0000-000000000000',
     'Why should vegetables be in a single layer on a roasting tray?',
     '["To roast rather than steam", "To cook faster", "To use less oil"]'::jsonb,
     'To roast rather than steam'),

    ('c5220000-0000-0000-0000-000000000000', 'a5220000-0000-0000-0000-000000000000',
     'How should tofu be prepared before marinating for best results?',
     '["Pressed to remove excess moisture", "Frozen first", "Crumbled finely"]'::jsonb,
     'Pressed to remove excess moisture'),

    ('c5310000-0000-0000-0000-000000000000', 'a5310000-0000-0000-0000-000000000000',
     'What type of fermentation is used to make sauerkraut?',
     '["Lacto-fermentation", "Acetic fermentation", "Alcoholic fermentation"]'::jsonb,
     'Lacto-fermentation'),

    ('c5320000-0000-0000-0000-000000000000', 'a5320000-0000-0000-0000-000000000000',
     'Which compound in mushrooms and aged cheeses is a major source of umami?',
     '["Glutamate", "Capsaicin", "Tannin"]'::jsonb, 'Glutamate')
ON CONFLICT DO NOTHING;
