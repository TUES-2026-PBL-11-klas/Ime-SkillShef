package com.skillchef.server.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Strongly-typed binding for {@code skillchef.auth.*} configuration. */
@ConfigurationProperties(prefix = "skillchef.auth")
public class AuthProperties {

    private final Jwt jwt = new Jwt();

    private Duration refreshTokenTtl = Duration.ofDays(30);
    private String appBaseUrl = "http://localhost:3000";

    public Jwt getJwt() {
        return jwt;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public static class Jwt {
        private String secret;
        private String issuer = "skillchef";
        private Duration accessTokenTtl = Duration.ofMinutes(15);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Duration getAccessTokenTtl() {
            return accessTokenTtl;
        }

        public void setAccessTokenTtl(Duration accessTokenTtl) {
            this.accessTokenTtl = accessTokenTtl;
        }
    }
}
