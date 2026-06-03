package com.skillchef.server.progression;

/**
 * Canonical XP &harr; level curve for the whole platform (issue #19).
 *
 * <p>Uses a triangular (RPG-style) curve: the XP cost of each level grows
 * linearly, so the cumulative XP required to <em>reach</em> level {@code L} is
 * <pre>{@code xpForLevel(L) = BASE * (L - 1) * L / 2}</pre>
 * With {@link #BASE} = 100 that yields thresholds 0, 100, 300, 600, 1000, ...
 *
 * <p>This is the single source of truth for XP-to-level math; other services
 * (e.g. XP awarding) delegate here.
 */
public final class LevelCurve {

    /** XP cost of the first level-up; each subsequent level costs {@code BASE} more. */
    public static final int BASE = 100;

    private LevelCurve() {
    }

    /**
     * The level reached for a given cumulative XP. Level 1 starts at 0 XP.
     *
     * @param totalXp cumulative XP (clamped to {@code >= 0})
     * @return the resulting level ({@code >= 1})
     */
    public static int levelForXp(int totalXp) {
        int safeXp = Math.max(0, totalXp);
        // Largest L with BASE*(L-1)*L/2 <= xp  =>  L = floor((1 + sqrt(1 + 8*xp/BASE)) / 2)
        int level = (int) Math.floor((1 + Math.sqrt(1 + (8.0 * safeXp) / BASE)) / 2);
        return Math.max(1, level);
    }

    /**
     * Cumulative XP required to reach the start of {@code level}.
     *
     * @param level the level ({@code >= 1})
     * @return the XP threshold for that level (level 1 = 0)
     */
    public static int xpForLevel(int level) {
        int safeLevel = Math.max(1, level);
        return BASE * (safeLevel - 1) * safeLevel / 2;
    }

    /** XP accumulated within the current level (i.e. since the current level began). */
    public static int xpIntoCurrentLevel(int totalXp) {
        int safeXp = Math.max(0, totalXp);
        return safeXp - xpForLevel(levelForXp(safeXp));
    }

    /** XP span of the current level, i.e. XP needed to advance from this level to the next. */
    public static int xpSpanOfCurrentLevel(int totalXp) {
        int level = levelForXp(totalXp);
        return xpForLevel(level + 1) - xpForLevel(level);
    }

    /** Remaining XP needed to reach the next level. */
    public static int xpToNextLevel(int totalXp) {
        int safeXp = Math.max(0, totalXp);
        return xpForLevel(levelForXp(safeXp) + 1) - safeXp;
    }

    /**
     * Progress through the current level as a 0&ndash;100 percentage.
     *
     * @param totalXp cumulative XP
     * @return percentage of the current level completed
     */
    public static int progressPercentToNextLevel(int totalXp) {
        int span = xpSpanOfCurrentLevel(totalXp);
        if (span <= 0) {
            return 0;
        }
        return (int) Math.round((xpIntoCurrentLevel(totalXp) * 100.0) / span);
    }
}
