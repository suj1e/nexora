package com.nexora.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Reactive JWT token provider for non-blocking JWT validation.
 *
 * <p>Wraps the blocking JWT operations in reactive types using
 * {@link Mono#fromCallable} with bounded elasticity scheduler.
 * This is essential for WebFlux and Gateway applications where blocking
 * operations can cause performance issues.
 *
 * <p>Usage:
 * <pre>
 * &#64;Autowired
 * private ReactiveJwtTokenProvider reactiveJwtProvider;
 *
 * // Validate token and get claims
 * reactiveJwtProvider.validateToken(token)
 *     .subscribe(claims -> processClaims(claims));
 *
 * // Check if token is valid (no exception)
 * reactiveJwtProvider.isValid(token)
 *     .subscribe(valid -> {
 *         if (valid) { // allow access }
 *     });
 * </pre>
 *
 * @author sujie
 */
@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "nexora.security.jwt", name = "enabled", havingValue = "true")
public class ReactiveJwtTokenProvider {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public ReactiveJwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        if (properties.getSecret() == null || properties.getSecret().isEmpty()) {
            throw new IllegalArgumentException("JWT secret must not be empty");
        }

        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        log.info("Initialized ReactiveJwtTokenProvider with issuer: {}", properties.getIssuer());
    }

    /**
     * Validate and parse JWT token (non-blocking).
     *
     * @param token the JWT token
     * @return Mono containing the token claims
     */
    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> {
            var parser = Jwts.parser()
                    .verifyWith(secretKey)
                    .build();

            Claims claims = parser.parseSignedClaims(token).getPayload();
            log.debug("JWT validated successfully for subject: {}", claims.getSubject());
            return claims;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(io.jsonwebtoken.JwtException.class, e -> {
            log.warn("JWT validation failed: {}", e.getMessage());
            return new JwtValidationException("Invalid JWT token: " + e.getMessage());
        });
    }

    /**
     * Get user ID from token.
     *
     * @param token the JWT token
     * @return Mono containing the user ID (subject)
     */
    public Mono<String> getUserId(String token) {
        return validateToken(token)
                .map(Claims::getSubject)
                .switchIfEmpty(Mono.error(new JwtValidationException("User ID not found in token")));
    }

    /**
     * Extract username from token.
     *
     * @param token the JWT token
     * @return Mono containing the username
     */
    public Mono<String> getUsername(String token) {
        return validateToken(token)
                .map(claims -> claims.get("username", String.class))
                .switchIfEmpty(Mono.error(new JwtValidationException("Username not found in token")));
    }

    /**
     * Extract specified claim from token.
     *
     * @param token the JWT token
     * @param claimName the claim name
     * @param clazz the claim type
     * @param <T> the claim type
     * @return Mono containing the claim value
     */
    public <T> Mono<T> getClaim(String token, String claimName, Class<T> clazz) {
        return validateToken(token)
                .map(claims -> claims.get(claimName, clazz));
    }

    /**
     * Check if token is valid (no exception thrown).
     *
     * @param token the JWT token
     * @return Mono emitting true if valid, false otherwise
     */
    public Mono<Boolean> isValid(String token) {
        return validateToken(token)
                .map(claims -> true)
                .onErrorReturn(false);
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return Mono emitting true if expired, false otherwise
     */
    public Mono<Boolean> isExpired(String token) {
        return validateToken(token)
                .map(claims -> claims.getExpiration().before(new Date()))
                .onErrorReturn(true);
    }

    /**
     * Get JWT properties.
     *
     * @return the JWT properties
     */
    public JwtProperties getProperties() {
        return properties;
    }

    /**
     * JWT validation exception.
     */
    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }
    }
}
