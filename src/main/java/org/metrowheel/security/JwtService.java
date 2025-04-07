package org.metrowheel.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.metrowheel.user.model.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service for handling JWT token generation and validation
 */
@ApplicationScoped
public class JwtService {

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://metrowheel.org")
    String issuer;

    @Inject
    @ConfigProperty(name = "jwt.duration.access", defaultValue = "86400") // 24 hours in seconds
    long accessTokenDuration;

    @Inject
    @ConfigProperty(name = "jwt.duration.refresh", defaultValue = "604800") // 7 days in seconds
    long refreshTokenDuration;

    /**
     * Generate a JWT access token for a user
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofSeconds(accessTokenDuration));
        
        Set<String> roles = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roles = new HashSet<>(Arrays.asList(user.getRoles().split(",")));
        } else {
            roles.add("user"); // Default role
        }
        
        return Jwt.issuer(issuer)
                .subject(user.getEmail())
                .groups(roles)
                .claim("email", user.getEmail())
                .claim("full_name", user.getFullName())
                .claim("user_id", user.getId().toString())
                .issuedAt(now)
                .expiresAt(expiry)
                .sign();
    }

    /**
     * Generate a refresh token for a user
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofSeconds(refreshTokenDuration));
        
        return Jwt.issuer(issuer)
                .subject(user.getEmail())
                .claim("email", user.getEmail())
                .claim("token_type", "refresh")
                .claim("user_id", user.getId().toString())
                .issuedAt(now)
                .expiresAt(expiry)
                .sign();
    }

    /**
     * Get the token expiration time in seconds
     */
    public long getAccessTokenDuration() {
        return accessTokenDuration;
    }

    /**
     * Parse a UUID from a string
     */
    public UUID parseUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + id);
        }
    }
}
