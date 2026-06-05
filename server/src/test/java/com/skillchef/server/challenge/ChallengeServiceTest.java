package com.skillchef.server.challenge;

import com.skillchef.server.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private ChallengeService service;

    @Test
    void submitStoresMediaAndPersistsSubmission() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(new Challenge()));
        when(storageService.store(eq("submissions"), any())).thenReturn("http://cdn/submissions/x.jpg");

        MultipartFile file = new MockMultipartFile("file", "dish.jpg", "image/jpeg", new byte[]{1, 2, 3});
        service.submit(userId, challengeId, file);

        verify(storageService).store(eq("submissions"), any());
        ArgumentCaptor<Submission> saved = ArgumentCaptor.forClass(Submission.class);
        verify(submissionRepository).save(saved.capture());
        assertThat(saved.getValue().getUserId()).isEqualTo(userId);
        assertThat(saved.getValue().getChallengeId()).isEqualTo(challengeId);
        assertThat(saved.getValue().getMediaUrl()).isEqualTo("http://cdn/submissions/x.jpg");
    }

    @Test
    void submitWithEmptyFileIsRejectedAndNothingStored() {
        UUID challengeId = UUID.randomUUID();
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(new Challenge()));
        MultipartFile empty = new MockMultipartFile("file", "x.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> service.submit(UUID.randomUUID(), challengeId, empty))
                .isInstanceOf(ChallengeException.class);

        verifyNoInteractions(storageService);
        verifyNoInteractions(submissionRepository);
    }

    @Test
    void getChallengeNotFoundThrows() {
        UUID challengeId = UUID.randomUUID();
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getChallenge(challengeId))
                .isInstanceOf(ChallengeException.class);
    }
}
