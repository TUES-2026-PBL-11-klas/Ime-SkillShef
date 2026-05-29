package com.skillchef.server.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Request/response payloads for the auth endpoints. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record SignupRequest(
            @NotBlank @Size(min = 3, max = 30) String username,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 100) String password) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record RefreshRequest(
            @NotBlank String refreshToken) {
    }

    public record LogoutRequest(
            @NotBlank String refreshToken) {
    }

    /** Returned on signup, login, and refresh. */
    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserSummary user) {

        public static AuthResponse of(String accessToken, String refreshToken,
                                      long expiresIn, UserSummary user) {
            return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
        }
    }

    public record UserSummary(
            UUID id,
            String username,
            String email,
            int globalXp,
            int level) {
    }

    public record MessageResponse(String message) {
    }
}
