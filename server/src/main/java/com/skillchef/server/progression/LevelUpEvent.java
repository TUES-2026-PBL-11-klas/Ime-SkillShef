package com.skillchef.server.progression;

import java.util.UUID;

/**
 * Published when a user's level increases as a result of an XP award (issue
 * #19). Other features (notifications, achievements, real-time updates) can
 * react by listening for this Spring application event.
 *
 * @param userId   the user who levelled up
 * @param oldLevel the level before the award
 * @param newLevel the level after the award ({@code > oldLevel})
 * @param totalXp  the user's cumulative XP after the award
 */
public record LevelUpEvent(UUID userId, int oldLevel, int newLevel, int totalXp) {
}
