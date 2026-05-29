package com.skillchef.server.auth.jwt;

import com.skillchef.server.auth.config.AuthProperties;
import com.skillchef.server.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/** Issues and verifies stateless access tokens (HMAC-signed JWTs). */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final String issuer;
    private final long accessTokenTtlSeconds;

    public JwtService(AuthProperties properties) {
        String secret = properties.getJwt().getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("skillchef.auth.jwt.secret must be configured");
        }
        byte[] keyBytes = decodeSecret(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = properties.getJwt().getIssuer();
        this.accessTokenTtlSeconds = properties.getJwt().getAccessTokenTtl().toSeconds();
    }

    public String issueAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenTtlSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    /**
     * Parses and verifies the token.
     *
     * @throws JwtException if the token is invalid, expired, or tampered with.
     */
    public ParsedToken parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        return new ParsedToken(userId, email);
    }

    private static byte[] decodeSecret(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            // Allow a raw (non-Base64) secret as a fallback.
            return secret.getBytes();
        }
    }

    public record ParsedToken(UUID userId, String email) {
    }
}
