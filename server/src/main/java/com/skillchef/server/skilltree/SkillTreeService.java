package com.skillchef.server.skilltree;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillchef.server.skilltree.SkillTreeDtos.DomainDto;
import com.skillchef.server.skilltree.SkillTreeDtos.LessonDto;
import com.skillchef.server.skilltree.SkillTreeDtos.NodeDto;
import com.skillchef.server.skilltree.SkillTreeDtos.ProgressDto;
import com.skillchef.server.skilltree.SkillTreeDtos.QuizDto;
import com.skillchef.server.skilltree.SkillTreeDtos.SkillTreeResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SkillTreeService {

    private final SkillDomainRepository skillDomainRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final SkillProgressRepository skillProgressRepository;
    private final ObjectMapper objectMapper;

    public SkillTreeService(SkillDomainRepository skillDomainRepository,
                            SkillNodeRepository skillNodeRepository,
                            LessonRepository lessonRepository,
                            QuizRepository quizRepository,
                            SkillProgressRepository skillProgressRepository,
                            ObjectMapper objectMapper) {
        this.skillDomainRepository = skillDomainRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.skillProgressRepository = skillProgressRepository;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "skillTree", key = "#userId")
    @Transactional(readOnly = true)
    public SkillTreeResponse getTree(UUID userId) {
        List<SkillDomain> domains = skillDomainRepository.findAll();
        List<SkillNode> nodes = skillNodeRepository.findAll();
        List<UUID> nodeIds = nodes.stream().map(SkillNode::getId).toList();
        List<Lesson> lessons = nodeIds.isEmpty()
                ? List.of()
                : lessonRepository.findByNodeIdIn(nodeIds);
        List<Quiz> quizzes = nodeIds.isEmpty()
                ? List.of()
                : quizRepository.findByNodeIdIn(nodeIds);
        List<SkillProgress> progressRows = skillProgressRepository.findByIdUserId(userId);

        Map<UUID, SkillProgress> progressByNode = progressRows.stream()
                .collect(Collectors.toMap(sp -> sp.getId().getNodeId(), sp -> sp));

        Map<UUID, List<LessonDto>> lessonsByNode = lessons.stream()
                .collect(Collectors.groupingBy(Lesson::getNodeId,
                        Collectors.mapping(this::toLessonDto, Collectors.toList())));

        Map<UUID, List<QuizDto>> quizzesByNode = quizzes.stream()
                .collect(Collectors.groupingBy(Quiz::getNodeId,
                        Collectors.mapping(this::toQuizDto, Collectors.toList())));

        Map<UUID, List<SkillNode>> nodesByDomain = nodes.stream()
                .collect(Collectors.groupingBy(SkillNode::getDomainId));

        Map<UUID, Map<Integer, Long>> totalByDomainTier = nodes.stream()
            .collect(Collectors.groupingBy(SkillNode::getDomainId,
                Collectors.groupingBy(SkillNode::getTier, Collectors.counting())));

        Map<UUID, Map<Integer, Long>> completedByDomainTier = nodes.stream()
            .filter(node -> {
                SkillProgress progress = progressByNode.get(node.getId());
                return progress != null && progress.getCompletedAt() != null;
            })
            .collect(Collectors.groupingBy(SkillNode::getDomainId,
                Collectors.groupingBy(SkillNode::getTier, Collectors.counting())));

        List<DomainDto> domainDtos = new ArrayList<>();
        for (SkillDomain domain : domains) {
            List<SkillNode> domainNodes = nodesByDomain.getOrDefault(domain.getId(), List.of());
            List<NodeDto> nodeDtos = domainNodes.stream()
                    .sorted(Comparator.comparingInt(SkillNode::getTier))
                    .map(node -> toNodeDto(node, progressByNode, lessonsByNode, quizzesByNode,
                        isUnlockable(node, totalByDomainTier, completedByDomainTier)))
                    .toList();
            domainDtos.add(new DomainDto(domain.getId(), domain.getName(), domain.getDescription(), nodeDtos));
        }

        return new SkillTreeResponse(domainDtos);
    }

    @CacheEvict(value = "skillTree", key = "#userId")
    @Transactional
    public ProgressDto unlockNode(UUID userId, UUID nodeId) {
        SkillNode node = skillNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill node not found"));
        SkillProgressId id = new SkillProgressId(userId, nodeId);
        SkillProgress progress = skillProgressRepository.findById(id)
                .orElseGet(() -> {
                    SkillProgress fresh = new SkillProgress();
                    fresh.setId(id);
                    return fresh;
                });

        if (progress.getUnlockedAt() != null || progress.getCompletedAt() != null) {
            return toProgressDto(progress);
        }

        if (!isUnlockable(userId, node)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prerequisites not met");
        }

        progress.setUnlockedAt(OffsetDateTime.now());
        skillProgressRepository.save(progress);
        return toProgressDto(progress);
    }

    @CacheEvict(value = "skillTree", key = "#userId")
    @Transactional
    public ProgressDto completeNode(UUID userId, UUID nodeId) {
        SkillProgressId id = new SkillProgressId(userId, nodeId);
        SkillProgress progress = skillProgressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill progress not found"));

        if (progress.getUnlockedAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill node is not unlocked");
        }

        progress.setProgressPercent(100);
        if (progress.getCompletedAt() == null) {
            progress.setCompletedAt(OffsetDateTime.now());
        }
        skillProgressRepository.save(progress);
        return toProgressDto(progress);
    }

    private NodeDto toNodeDto(SkillNode node,
                              Map<UUID, SkillProgress> progressByNode,
                              Map<UUID, List<LessonDto>> lessonsByNode,
                              Map<UUID, List<QuizDto>> quizzesByNode,
                              boolean unlockable) {
        SkillProgress progress = progressByNode.get(node.getId());
        ProgressDto progressDto = progress == null
                ? new ProgressDto(0, null, null)
                : toProgressDto(progress);
        List<LessonDto> lessonDtos = lessonsByNode.getOrDefault(node.getId(), List.of());
        List<QuizDto> quizDtos = quizzesByNode.getOrDefault(node.getId(), List.of());

        return new NodeDto(node.getId(),
                node.getDomainId(),
                node.getParentNodeId(),
                node.getTitle(),
                node.getDescription(),
                node.getTier(),
                node.getXpReward(),
                unlockable,
                progressDto,
                lessonDtos,
                quizDtos);
    }

    private ProgressDto toProgressDto(SkillProgress progress) {
        return new ProgressDto(progress.getProgressPercent(),
                progress.getUnlockedAt(),
                progress.getCompletedAt());
    }

    private LessonDto toLessonDto(Lesson lesson) {
        return new LessonDto(lesson.getId(), lesson.getNodeId(), lesson.getTitle(),
                lesson.getDescription(), lesson.getVideoUrl(), lesson.getDurationSeconds());
    }

    private QuizDto toQuizDto(Quiz quiz) {
        List<String> options = parseOptions(quiz.getOptions());
        return new QuizDto(quiz.getId(), quiz.getNodeId(), quiz.getQuestion(), options);
    }

    private List<String> parseOptions(String rawOptions) {
        if (rawOptions == null || rawOptions.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(rawOptions, new TypeReference<List<String>>() {
            });
        } catch (java.io.IOException | RuntimeException ex) {
            return List.of();
        }
    }

    private boolean isUnlockable(UUID userId, SkillNode node) {
        if (node.getTier() <= 1) {
            return true;
        }
        long required = skillNodeRepository.countByDomainIdAndTierLessThan(node.getDomainId(), node.getTier());
        if (required == 0) {
            return true;
        }
        long completed = skillNodeRepository.countCompletedByUserAndDomainAndTierLessThan(
                userId, node.getDomainId(), node.getTier());
        return completed >= required;
    }

    private boolean isUnlockable(SkillNode node,
                                 Map<UUID, Map<Integer, Long>> totalByDomainTier,
                                 Map<UUID, Map<Integer, Long>> completedByDomainTier) {
        if (node.getTier() <= 1) {
            return true;
        }
        Map<Integer, Long> totals = totalByDomainTier.getOrDefault(node.getDomainId(), Map.of());
        Map<Integer, Long> completed = completedByDomainTier.getOrDefault(node.getDomainId(), Map.of());

        for (int tier = 1; tier < node.getTier(); tier++) {
            long required = totals.getOrDefault(tier, 0L);
            if (required == 0) {
                continue;
            }
            long done = completed.getOrDefault(tier, 0L);
            if (done < required) {
                return false;
            }
        }

        return true;
    }
}
