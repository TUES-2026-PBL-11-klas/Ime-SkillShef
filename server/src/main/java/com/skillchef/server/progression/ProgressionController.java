package com.skillchef.server.progression;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import com.skillchef.server.progression.ProgressionDtos.LeaderboardResponse;
import com.skillchef.server.progression.ProgressionDtos.LevelProgressResponse;
import com.skillchef.server.progression.ProgressionDtos.XpHistoryResponse;

/**
 * Progression endpoints (issue #19): the caller's level progress, their XP
 * history, their leaderboard rank, and the global leaderboard.
 */
@RestController
@RequestMapping("/api/progression")
public class ProgressionController {

    private final ProgressionService progressionService;

    public ProgressionController(ProgressionService progressionService) {
        this.progressionService = progressionService;
    }

    @GetMapping("/me")
    public LevelProgressResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
        UUID userId = requireUser(principal);
        return progressionService.getProgression(userId);
    }

    @GetMapping("/me/rank")
    public RankResponse myRank(@AuthenticationPrincipal AuthPrincipal principal) {
        UUID userId = requireUser(principal);
        return new RankResponse(progressionService.rankOf(userId));
    }

    @GetMapping("/me/xp-history")
    public XpHistoryResponse myXpHistory(@AuthenticationPrincipal AuthPrincipal principal,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        UUID userId = requireUser(principal);
        return progressionService.xpHistory(userId, page, size);
    }

    @GetMapping("/leaderboard")
    public LeaderboardResponse leaderboard(@AuthenticationPrincipal AuthPrincipal principal,
                                           @RequestParam(defaultValue = "20") int limit) {
        requireUser(principal);
        return progressionService.leaderboard(limit);
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }

    /** Simple wrapper for the caller's leaderboard rank. */
    public record RankResponse(long rank) {
    }
}
