package com.skillchef.server.user;

import com.skillchef.server.user.SocialDtos.FollowCountsResponse;
import com.skillchef.server.user.SocialDtos.FollowStatusResponse;
import com.skillchef.server.user.SocialDtos.PageResponse;
import com.skillchef.server.user.SocialDtos.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Application logic for the Social Service: follow/unfollow, followers and
 * following lists, "is following" checks, follower counts, and the follows
 * feed query consumed by the community feed (Person 4).
 */
@Service
public class SocialService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public SocialService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    /** Follow {@code targetId} as {@code followerId}. Idempotent. */
    @Transactional
    public void follow(UUID followerId, UUID targetId) {
        if (followerId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself");
        }
        requireUserExists(targetId);

        FollowId id = new FollowId(followerId, targetId);
        if (!followRepository.existsById(id)) {
            followRepository.save(new Follow(id));
        }
    }

    /** Unfollow {@code targetId} as {@code followerId}. Idempotent. */
    @Transactional
    public void unfollow(UUID followerId, UUID targetId) {
        FollowId id = new FollowId(followerId, targetId);
        if (followRepository.existsById(id)) {
            followRepository.deleteById(id);
        }
    }

    /** Whether {@code followerId} currently follows {@code targetId}. */
    @Transactional(readOnly = true)
    public FollowStatusResponse isFollowing(UUID followerId, UUID targetId) {
        boolean following = followRepository.existsById(new FollowId(followerId, targetId));
        return new FollowStatusResponse(following);
    }

    /** Follower and following counts for {@code userId}. */
    @Transactional(readOnly = true)
    public FollowCountsResponse counts(UUID userId) {
        requireUserExists(userId);
        return new FollowCountsResponse(
                userId,
                followRepository.countByIdFollowingId(userId),
                followRepository.countByIdFollowerId(userId));
    }

    /** Paged list of users following {@code userId}, most recent first. */
    @Transactional(readOnly = true)
    public PageResponse<UserSummary> followers(UUID userId, int page, int size) {
        requireUserExists(userId);
        return toPage(followRepository.findFollowers(userId, pageable(page, size)));
    }

    /** Paged list of users that {@code userId} follows, most recent first. */
    @Transactional(readOnly = true)
    public PageResponse<UserSummary> following(UUID userId, int page, int size) {
        requireUserExists(userId);
        return toPage(followRepository.findFollowing(userId, pageable(page, size)));
    }

    /**
     * Ids of every user that {@code userId} follows. The follows feed query that the
     * community feed (Person 4) joins against to pull recipes from followed users.
     */
    @Transactional(readOnly = true)
    public List<UUID> followingIds(UUID userId) {
        return followRepository.findFollowingIds(userId);
    }

    private Pageable pageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(safePage, safeSize);
    }

    private PageResponse<UserSummary> toPage(Page<User> users) {
        List<UserSummary> items = users.getContent().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getAvatarUrl(), u.getLevel()))
                .toList();
        return new PageResponse<>(
                items,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages());
    }

    private void requireUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
