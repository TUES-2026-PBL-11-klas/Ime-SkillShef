package com.skillchef.server.skilltree;

/**
 * Simple, deterministic XP &rarr; level mapping shared by the learning-content
 * features. Each level costs a flat {@link #XP_PER_LEVEL} amount of XP.
 *
 * <p>The richer progression rules (leaderboards, curved formulas, etc.) live in
 * the dedicated Progression Service (issue #19); this helper only provides the
 * minimal mapping needed when XP is awarded for passing a quiz.
 */
public final class LevelFormula {

    /** XP required to advance one level. */
    public static final int XP_PER_LEVEL = 100;

    private LevelFormula() {
    }

    /**
     * Computes the level for a given total XP. Level 1 starts at 0 XP.
     *
     * @param totalXp cumulative XP (clamped to {@code >= 0})
     * @return the resulting level ({@code >= 1})
     */
    public static int levelForXp(int totalXp) {
        int safeXp = Math.max(0, totalXp);
        return 1 + (safeXp / XP_PER_LEVEL);
    }
}
