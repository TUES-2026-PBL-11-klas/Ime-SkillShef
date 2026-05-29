package com.skillchef.server.auth;

import com.skillchef.server.auth.config.AuthProperties;
import com.skillchef.server.auth.dto.AuthDtos.AuthResponse;
import com.skillchef.server.auth.dto.AuthDtos.LoginRequest;
import com.skillchef.server.auth.dto.AuthDtos.LogoutRequest;
import com.skillchef.server.auth.dto.AuthDtos.RefreshRequest;
import com.skillchef.server.auth.dto.AuthDtos.SignupRequest;
import com.skillchef.server.auth.dto.AuthDtos.UserSummary;
import com.skillchef.server.auth.jwt.JwtService;
import com.skillchef.server.auth.token.RefreshToken;
import com.skillchef.server.auth.token.RefreshTokenRepository;
import com.skillchef.server.auth.token.TokenGenerator;
import com.skillchef.server.user.User;
import com.skillchef.server.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Application logic for the auth use-cases: signup, login, logout, and token
 * refresh. Controllers parse/validate; this layer owns the rules and persistence.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenGenerator tokenGenerator;
    private final AuthProperties properties;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       TokenGenerator tokenGenerator,
                       AuthProperties properties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenGenerator = tokenGenerator;
        this.properties = properties;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw AuthException.conflict("An account with this email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw AuthException.conflict("This username is taken");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .orElse(null);
        // Run the hash comparison even when the user is missing to avoid leaking
        // which emails exist via response timing.
        boolean matches = user != null
                && passwordEncoder.matches(request.password(), user.getPasswordHash());
        if (!matches) {
            throw AuthException.unauthorized("Invalid email or password");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String hash = tokenGenerator.hash(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> AuthException.unauthorized("Invalid refresh token"));
        if (!stored.isActive()) {
            throw AuthException.unauthorized("Refresh token is expired or revoked");
        }
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> AuthException.unauthorized("Account no longer exists"));

        // Rotate: revoke the presented token and issue a fresh pair.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String hash = tokenGenerator.hash(request.refreshToken());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
        // Always succeeds: logging out an unknown/expired token is a no-op.
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.issueAccessToken(user);
        String rawRefresh = tokenGenerator.generateRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setTokenHash(tokenGenerator.hash(rawRefresh));
        refreshToken.setExpiresAt(OffsetDateTime.now().plus(properties.getRefreshTokenTtl()));
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.of(accessToken, rawRefresh,
                jwtService.getAccessTokenTtlSeconds(), toSummary(user));
    }

    @Transactional(readOnly = true)
    public UserSummary currentUser(UUID userId) {
        return userRepository.findById(userId)
                .map(AuthService::toSummary)
                .orElseThrow(() -> AuthException.unauthorized("Account no longer exists"));
    }

    private static UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getUsername(), user.getEmail(),
                user.getGlobalXp(), user.getLevel());
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
