package com.skillchef.server.progression;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillchef.server.progression.ProgressionDtos.LeaderboardEntry;
import com.skillchef.server.progression.ProgressionDtos.LeaderboardResponse;
import com.skillchef.server.progression.ProgressionDtos.LevelProgressResponse;
import com.skillchef.server.progression.ProgressionDtos.XpHistoryResponse;
import com.skillchef.server.progression.ProgressionDtos.XpTransactionDto;
import com.skillchef.server.skilltree.XpTransaction;
import com.skillchef.server.skilltree.XpTransactionRepository;
import com.skillchef.server.user.User;
import com.skillchef.server.user.UserRepository;

/**
 * Progression Service (issue #19): exposes a user's position on the XP curve,
 * the global leaderboard, and a user's XP transaction history. The XP-to-level
 * math is delegated to {@link LevelCurve}; level-up events are emitted from the
 * XP awarding path (see {@code XpAwardService}).
 */
@Service
public class ProgressionService {

    /** Hard cap on how many leaderboard rows a single query may return. */
    static final int MAX_LEADERBOARD_LIMIT = 100;
    /** Hard cap on how many XP history rows a single query may return. */
    static final int MAX_HISTORY_SIZE = 100;

    private final UserRepository userRepository;
    private final XpTransactionRepository xpTransactionRepository;

    public ProgressionService(UserRepository userRepository,
                              XpTransactionRepository xpTransactionRepository) {
        this.userRepository = userRepository;
        this.xpTransactionRepository = xpTransactionRepository;
    }

    /** The user's current XP/level standing, including progress to the next level. */
    @Transactional(readOnly = true)
    public LevelProgressResponse getProgression(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int xp = user.getGlobalXp();
        int level = LevelCurve.levelForXp(xp);
        return new LevelProgressResponse(
                user.getId(),
                xp,
                level,
                LevelCurve.xpIntoCurrentLevel(xp),
                LevelCurve.xpForLevel(level),
                LevelCurve.xpForLevel(level + 1),
                LevelCurve.xpToNextLevel(xp),
                LevelCurve.progressPercentToNextLevel(xp));
    }

    /** Top users ordered by XP (descending), ranked from 1. */
    @Transactional(readOnly = true)
    public LeaderboardResponse leaderboard(int limit) {
        int safeLimit = Math.min(Math.max(1, limit), MAX_LEADERBOARD_LIMIT);
        List<User> top = userRepository
                .findAllByOrderByGlobalXpDescUsernameAsc(PageRequest.of(0, safeLimit));

        List<LeaderboardEntry> entries = new ArrayList<>(top.size());
        long rank = 1;
        for (User user : top) {
            entries.add(new LeaderboardEntry(
                    rank++,
                    user.getId(),
                    user.getUsername(),
                    user.getAvatarUrl(),
                    user.getGlobalXp(),
                    LevelCurve.levelForXp(user.getGlobalXp())));
        }
        return new LeaderboardResponse(entries);
    }

    /** The user's global leaderboard rank (1-based), computed from XP. */
    @Transactional(readOnly = true)
    public long rankOf(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userRepository.countByGlobalXpGreaterThan(user.getGlobalXp()) + 1;
    }

    /** The user's XP transaction history, newest first, paginated. */
    @Transactional(readOnly = true)
    public XpHistoryResponse xpHistory(UUID userId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_HISTORY_SIZE);

        List<XpTransactionDto> transactions = xpTransactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(safePage, safeSize)).stream()
                .map(this::toDto)
                .toList();
        return new XpHistoryResponse(transactions);
    }

    private XpTransactionDto toDto(XpTransaction tx) {
        return new XpTransactionDto(
                tx.getId(),
                tx.getAmount(),
                tx.getReason(),
                tx.getSourceType(),
                tx.getSourceId(),
                tx.getCreatedAt());
    }
}
