package com.skillchef.server.user;

import java.util.List;
import java.util.UUID;

/** Request/response payloads for the Social Service (follows). */
public final class SocialDtos {

    private SocialDtos() {
    }

    /** Compact view of a user in a followers/following list. */
    public record UserSummary(
            UUID id,
            String username,
            String avatarUrl,
            int level) {
    }

    /** Whether the authenticated user is following a given target user. */
    public record FollowStatusResponse(boolean following) {
    }

    /** Follower / following counts for a user. */
    public record FollowCountsResponse(
            UUID userId,
            long followers,
            long following) {
    }

    /** A single page of results, kept serializable and stable for clients. */
    public record PageResponse<T>(
            List<T> items,
            int page,
            int size,
            long totalElements,
            int totalPages) {
    }
}
