package com.skillchef.server.progression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Default in-process reaction to {@link LevelUpEvent}. For now this simply logs
 * the level-up; downstream features (notifications, achievements) can add their
 * own listeners without touching the XP awarding path.
 */
@Component
public class LevelUpEventListener {

    private static final Logger log = LoggerFactory.getLogger(LevelUpEventListener.class);

    @EventListener
    public void onLevelUp(LevelUpEvent event) {
        log.info("User {} levelled up: {} -> {} (totalXp={})",
                event.userId(), event.oldLevel(), event.newLevel(), event.totalXp());
    }
}
