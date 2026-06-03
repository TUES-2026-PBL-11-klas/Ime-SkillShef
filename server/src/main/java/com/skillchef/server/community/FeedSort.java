package com.skillchef.server.community;

/** Feed sort modes accepted by {@code GET /api/feed}. */
public enum FeedSort {
    /** Posts from followed accounts, newest first. Requires authentication. */
    RECENT,
    /** All posts sorted by like count in the last 24 hours. Public. */
    TRENDING
}
