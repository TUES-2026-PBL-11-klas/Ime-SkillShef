package com.skillchef.server.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Request/response payloads for the user/profile endpoints. */
public final class UserDtos {

    private UserDtos() {
    }

    /** Preferences stored as JSON in {@code profiles.preferences}. */
    public record PreferencesDto(
            boolean notifications,
            @NotBlank @Pattern(regexp = "light|dark|system", message = "must be one of: light, dark, system")
            String theme,
            @NotBlank @Size(min = 2, max = 10) String language) {

        public static PreferencesDto defaults() {
            return new PreferencesDto(true, "system", "en");
        }
    }

    /** Full profile of the authenticated user (own view). */
    public record ProfileResponse(
            UUID id,
            String username,
            String email,
            String avatarUrl,
            String bio,
            PreferencesDto preferences,
            int globalXp,
            int level,
            OffsetDateTime createdAt) {
    }

    /** Public view of another user's profile. */
    public record PublicProfileResponse(
            UUID id,
            String username,
            String avatarUrl,
            String bio,
            int globalXp,
            int level,
            OffsetDateTime createdAt) {
    }

    /** XP and level read endpoint payload. */
    public record XpLevelResponse(
            UUID userId,
            int globalXp,
            int level) {
    }

    /** Profile update (own profile). All fields optional; null leaves the value unchanged. */
    public record UpdateProfileRequest(
            @Size(min = 3, max = 30) String username,
            @Size(max = 2000) String bio,
            @Valid PreferencesDto preferences) {
    }

    /** Avatar upload response. */
    public record AvatarResponse(String avatarUrl) {
    }
}
