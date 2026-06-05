package com.skillchef.server.challenge;

import com.skillchef.server.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.skillchef.server.challenge.ChallengeDtos.ChallengeDetailResponse;
import static com.skillchef.server.challenge.ChallengeDtos.ChallengeResponse;
import static com.skillchef.server.challenge.ChallengeDtos.SubmissionResponse;

/**
 * Application logic for challenges and their submissions.
 *
 * <p>Submission media is persisted through the shared {@link StorageService}
 * (Person 4's storage layer) under the {@code submissions} folder; we store the
 * returned URL on the {@code submissions.media_url} column.
 */
@Service
public class ChallengeService {

    private static final String SUBMISSION_FOLDER = "submissions";

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final StorageService storageService;

    public ChallengeService(ChallengeRepository challengeRepository,
                            SubmissionRepository submissionRepository,
                            StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponse> listChallenges() {
        OffsetDateTime now = OffsetDateTime.now();
        return challengeRepository.findAllByOrderByStartDateDesc().stream()
                .map(c -> toResponse(c, now, submissionRepository.countByChallengeId(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChallengeDetailResponse getChallenge(UUID challengeId) {
        Challenge challenge = requireChallenge(challengeId);
        List<SubmissionResponse> submissions = submissionRepository
                .findByChallengeIdOrderByCreatedAtDesc(challengeId).stream()
                .map(ChallengeService::toSubmissionResponse)
                .toList();
        return new ChallengeDetailResponse(challenge.getId(), challenge.getTitle(), challenge.getType(),
                challenge.getXpReward(), challenge.getStartDate(), challenge.getEndDate(),
                isActive(challenge, OffsetDateTime.now()), submissions);
    }

    /** Store the uploaded media and record the user's submission for a challenge. */
    @Transactional
    public void submit(UUID userId, UUID challengeId, MultipartFile file) {
        requireChallenge(challengeId);
        if (file == null || file.isEmpty()) {
            throw ChallengeException.badRequest("A photo or video of your result is required");
        }
        String mediaUrl = storageService.store(SUBMISSION_FOLDER, file);

        Submission submission = new Submission();
        submission.setChallengeId(challengeId);
        submission.setUserId(userId);
        submission.setMediaUrl(mediaUrl);
        submissionRepository.save(submission);
    }

    private Challenge requireChallenge(UUID challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> ChallengeException.notFound("Challenge not found"));
    }

    private static boolean isActive(Challenge challenge, OffsetDateTime now) {
        return !now.isBefore(challenge.getStartDate()) && !now.isAfter(challenge.getEndDate());
    }

    private static ChallengeResponse toResponse(Challenge c, OffsetDateTime now, long submissionCount) {
        return new ChallengeResponse(c.getId(), c.getTitle(), c.getType(), c.getXpReward(),
                c.getStartDate(), c.getEndDate(), isActive(c, now), submissionCount);
    }

    private static SubmissionResponse toSubmissionResponse(Submission s) {
        return new SubmissionResponse(s.getId(), s.getChallengeId(), s.getUserId(), s.getMediaUrl(),
                s.getAiFeedback(), s.getScore(), s.getCreatedAt());
    }
}
