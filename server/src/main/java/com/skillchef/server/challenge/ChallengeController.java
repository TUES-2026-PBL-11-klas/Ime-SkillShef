package com.skillchef.server.challenge;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.skillchef.server.challenge.ChallengeDtos.ChallengeDetailResponse;
import static com.skillchef.server.challenge.ChallengeDtos.ChallengeResponse;

/**
 * Challenges browsing and submission flow.
 *
 * <p>The submission endpoint accepts the media file directly (multipart) and
 * re-reads the challenge afterwards so the response carries the new entry with
 * its database-generated timestamp.
 */
@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public List<ChallengeResponse> listChallenges() {
        return challengeService.listChallenges();
    }

    @GetMapping("/{challengeId}")
    public ChallengeDetailResponse getChallenge(@PathVariable UUID challengeId) {
        return challengeService.getChallenge(challengeId);
    }

    @PostMapping("/{challengeId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeDetailResponse submit(
            @PathVariable UUID challengeId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AuthPrincipal principal) {
        challengeService.submit(principal.userId(), challengeId, file);
        return challengeService.getChallenge(challengeId);
    }
}
