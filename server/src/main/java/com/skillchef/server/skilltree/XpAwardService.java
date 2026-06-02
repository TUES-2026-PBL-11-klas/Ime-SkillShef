package com.skillchef.server.skilltree;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillchef.server.user.User;
import com.skillchef.server.user.UserRepository;

/**
 * Centralises XP awarding so every grant is recorded in {@code xp_transactions}
 * and reflected on the user's {@code global_xp} / {@code level}. Used by the
 * Quiz service when a user passes a quiz for the first time.
 */
@Service
public class XpAwardService {

    private final XpTransactionRepository xpTransactionRepository;
    private final UserRepository userRepository;

    public XpAwardService(XpTransactionRepository xpTransactionRepository,
                          UserRepository userRepository) {
        this.xpTransactionRepository = xpTransactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Records an XP grant and updates the user's running total and level.
     *
     * @param userId     the user receiving XP
     * @param amount     XP to add (must be {@code > 0})
     * @param reason     short machine-friendly reason, e.g. {@code "quiz_passed"}
     * @param sourceType the source category, e.g. {@code "QUIZ"}
     * @param sourceId   the source entity id (nullable)
     * @return the updated XP/level snapshot for the user
     */
    @Transactional
    public Result award(UUID userId, int amount, String reason, String sourceType, UUID sourceId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("XP amount must be positive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        XpTransaction tx = new XpTransaction();
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setReason(reason);
        tx.setSourceType(sourceType);
        tx.setSourceId(sourceId);
        xpTransactionRepository.save(tx);

        int newXp = user.getGlobalXp() + amount;
        user.setGlobalXp(newXp);
        user.setLevel(LevelFormula.levelForXp(newXp));
        userRepository.save(user);

        return new Result(amount, newXp, user.getLevel());
    }

    /** Snapshot of the effect of an XP award. */
    public record Result(int awardedXp, int newGlobalXp, int newLevel) {
    }
}
