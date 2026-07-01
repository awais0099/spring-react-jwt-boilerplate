package com.project.auth_app_backend.security;

import com.project.auth_app_backend.entities.Role;
import com.project.auth_app_backend.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Setter
@Getter
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${security.jwt.issuer}") String issuer) {

        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException("Invalid cryptographic secret. Must be at least 256 bits (64 characters).");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    /**
     * Generates a short-lived Access Token containing subject identity, email, and user authorities.
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        
        // Ensures roles carry the mandatory 'ROLE_' prefix for Spring Method Security evaluations
        List<String> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream()
                        .map(Role::getName)
                        .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                        .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("typ", "access")
                .signWith(key)
                .compact();
    }

    /**
     * Generates a long-lived Refresh Token linked directly to a persistence database sequence via tracking ID (jti).
     */
    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public boolean isAccessToken(String token) {
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String token) {
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }

    public UUID getUserId(String token) {
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    /**
     * Safely reads the user roles claim collection, eliminating casting warning errors.
     */
    public List<String> getRoles(String token) {
        Claims claims = parse(token).getPayload();
        Object rolesObject = claims.get("roles");
        
        if (rolesObject instanceof Collection<?>) {
            return ((Collection<?>) rolesObject).stream()
                    .map(Object::toString)
                    .toList();
        }
        return List.of();
    }

    public String getEmail(String token) {
        Claims c = parse(token).getPayload();
        return (String) c.get("email");
    }

    public Date extractExpiration(String token) {
        Claims c = parse(token).getPayload();
        return c.getExpiration();
    }
}