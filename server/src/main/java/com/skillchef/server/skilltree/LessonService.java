package com.skillchef.server.skilltree;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillchef.server.skilltree.LessonQuizDtos.LessonPlaybackResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonResponse;
import com.skillchef.server.skilltree.LessonQuizDtos.LessonUpdateRequest;
import com.skillchef.server.skilltree.LessonQuizDtos.WatchResponse;

/**
 * Lesson management and playback for the learning-content domain (issue #15):
 * lesson CRUD, listing per skill node with a per-user "watched" overlay, a
 * playback endpoint returning the video (Mux/CDN) URL, and watched tracking.
 */
@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final LessonWatchRepository lessonWatchRepository;

    public LessonService(LessonRepository lessonRepository,
                         SkillNodeRepository skillNodeRepository,
                         LessonWatchRepository lessonWatchRepository) {
        this.lessonRepository = lessonRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.lessonWatchRepository = lessonWatchRepository;
    }

    @Transactional
    public LessonResponse create(LessonRequest request) {
        requireNode(request.nodeId());

        Lesson lesson = new Lesson();
        lesson.setNodeId(request.nodeId());
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setDurationSeconds(request.durationSeconds());
        Lesson saved = lessonRepository.save(lesson);

        return toResponse(saved, false, null);
    }

    @Transactional
    public LessonResponse update(UUID lessonId, LessonUpdateRequest request) {
        Lesson lesson = requireLesson(lessonId);
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setDurationSeconds(request.durationSeconds());
        Lesson saved = lessonRepository.save(lesson);
        return toResponse(saved, false, null);
    }

    @Transactional
    public void delete(UUID lessonId) {
        Lesson lesson = requireLesson(lessonId);
        lessonRepository.delete(lesson);
    }

    @Transactional(readOnly = true)
    public LessonResponse get(UUID userId, UUID lessonId) {
        Lesson lesson = requireLesson(lessonId);
        LessonWatch watch = lessonWatchRepository
                .findById(new LessonWatchId(userId, lessonId))
                .orElse(null);
        return toResponse(lesson, watch != null, watch != null ? watch.getWatchedAt() : null);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> listByNode(UUID userId, UUID nodeId) {
        requireNode(nodeId);
        List<Lesson> lessons = lessonRepository.findByNodeIdOrderByCreatedAt(nodeId);
        if (lessons.isEmpty()) {
            return List.of();
        }

        List<UUID> lessonIds = lessons.stream().map(Lesson::getId).toList();
        Set<UUID> watched = lessonWatchRepository
                .findByIdUserIdAndIdLessonIdIn(userId, lessonIds).stream()
                .map(w -> w.getId().getLessonId())
                .collect(Collectors.toCollection(HashSet::new));

        return lessons.stream()
                .map(lesson -> toResponse(lesson, watched.contains(lesson.getId()), null))
                .toList();
    }

    /** Returns the playback (Mux/CDN) URL for a lesson. */
    @Transactional(readOnly = true)
    public LessonPlaybackResponse getPlayback(UUID lessonId) {
        Lesson lesson = requireLesson(lessonId);
        return new LessonPlaybackResponse(lesson.getId(), lesson.getVideoUrl(), lesson.getDurationSeconds());
    }

    /** Marks the lesson as watched for the user (idempotent upsert). */
    @CacheEvict(value = "skillTree", key = "#userId")
    @Transactional
    public WatchResponse markWatched(UUID userId, UUID lessonId) {
        requireLesson(lessonId);

        LessonWatchId id = new LessonWatchId(userId, lessonId);
        OffsetDateTime now = OffsetDateTime.now();
        LessonWatch watch = lessonWatchRepository.findById(id)
                .orElseGet(() -> {
                    LessonWatch fresh = new LessonWatch();
                    fresh.setId(id);
                    return fresh;
                });
        watch.setWatchedAt(now);
        LessonWatch saved = lessonWatchRepository.save(watch);

        return new WatchResponse(lessonId, true, saved.getWatchedAt());
    }

    private void requireNode(UUID nodeId) {
        if (!skillNodeRepository.existsById(nodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill node not found");
        }
    }

    private Lesson requireLesson(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
    }

    private LessonResponse toResponse(Lesson lesson, boolean watched, OffsetDateTime watchedAt) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getNodeId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getVideoUrl(),
                lesson.getDurationSeconds(),
                watched,
                watchedAt);
    }
}
