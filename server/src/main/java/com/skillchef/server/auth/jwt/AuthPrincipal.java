package com.skillchef.server.auth.jwt;

import java.util.UUID;

/**
 * The authenticated user as resolved from a verified access token. Other
 * feature modules obtain this via {@code @AuthenticationPrincipal AuthPrincipal}
 * on protected controller methods.
 */
public record AuthPrincipal(UUID userId, String email) {
}
