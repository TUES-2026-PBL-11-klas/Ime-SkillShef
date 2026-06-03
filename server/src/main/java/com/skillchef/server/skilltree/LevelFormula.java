package com.skillchef.server.skilltree;

import com.skillchef.server.progression.LevelCurve;

/**
 * Thin compatibility shim that delegates to the canonical
 * {@link LevelCurve} (issue #19). Retained so existing call sites keep working;
 * prefer using {@link LevelCurve} directly in new code.
 *
 * @deprecated use {@link LevelCurve} instead.
 */
@Deprecated
public final class LevelFormula {

    private LevelFormula() {
    }

    /**
     * Computes the level for a given total XP. Level 1 starts at 0 XP.
     *
     * @param totalXp cumulative XP (clamped to {@code >= 0})
     * @return the resulting level ({@code >= 1})
     */
    public static int levelForXp(int totalXp) {
        return LevelCurve.levelForXp(totalXp);
    }
}
