package org.metrowheel.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.metrowheel.user.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for JWT token generation and validation using jjwt library
 */
@ApplicationScoped
public class TokenUtils {

    private static final String ROLES_CLAIM = "roles";
    private static final String USER_ID_CLAIM = "userId";
    private static final String EMAIL_CLAIM = "email";
    private static final String FULL_NAME_CLAIM = "fullName";
    
    // We'll use a simple shared secret key instead of complex RSA keys for now
    private static final String DEFAULT_SECRET = "metrowheel_default_jwt_secret_key_this_should_be_at_least_256_bits_long";
    
    @Inject
    @ConfigProperty(name = "jwt.secret", defaultValue = DEFAULT_SECRET)
    String jwtSecret;
    
    @Inject
    @ConfigProperty(name = "jwt.duration.access", defaultValue = "86400")
    long accessTokenDuration;
    
    @Inject
    @ConfigProperty(name = "jwt.duration.refresh", defaultValue = "604800")
    long refreshTokenDuration;
    
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://metrowheel.org")
    String issuer;

    /**
     * Generate an access token for a user
     */
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenDuration);
    }
    
    /**
     * Generate a refresh token for a user
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenDuration);
    }
    
    /**
     * Get the token expiration time in seconds
     */
    public long getAccessTokenDuration() {
        return accessTokenDuration;
    }
    
    /**
     * Generate a JWT token with the specified expiration duration
     */
    private String generateToken(User user, long durationSeconds) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plus(durationSeconds, ChronoUnit.SECONDS));
        
        // Create a secret key from our secret string
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        // Parse roles
        Set<String> roles = Stream.of(user.getRoles().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        
        // Build the JWT
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer(issuer)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim(USER_ID_CLAIM, user.getId().toString())
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(FULL_NAME_CLAIM, user.getFullName())
                .claim(ROLES_CLAIM, String.join(",", roles))
                .signWith(key)
                .compact();
        
        return token;
    }
    
    /**
     * Parse and validate a JWT token
     * 
     * @param token The JWT token to parse
     * @return The parsed claims if the token is valid
     * @throws Exception If the token is invalid or expired
     */
    public io.jsonwebtoken.Claims parseToken(String token) throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
