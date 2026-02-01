package com.nexora.security.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Refresh token entity for JWT refresh token management.
 *
 * <p>Stores refresh tokens with expiration tracking and user association.
 * Allows revocation of refresh tokens for security purposes.
 *
 * @author sujie
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token_user_id", columnList = "user_id"),
    @Index(name = "idx_refresh_token_token", columnList = "token"),
    @Index(name = "idx_refresh_token_expiry", columnList = "expiry_date")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The refresh token value.
     */
    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    /**
     * The user ID associated with this refresh token.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * When this refresh token was created.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate = Instant.now();

    /**
     * When this refresh token expires.
     */
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    /**
     * Whether this refresh token has been revoked.
     */
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    /**
     * When this refresh token was revoked (null if not revoked).
     */
    @Column(name = "revoked_date")
    private Instant revokedDate;

    /**
     * Check if this refresh token is expired.
     *
     * @return true if expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    /**
     * Check if this refresh token is valid (not expired and not revoked).
     *
     * @return true if valid
     */
    public boolean isValid() {
        return !isExpired() && !revoked;
    }

    /**
     * Revoke this refresh token.
     */
    public void revoke() {
        this.revoked = true;
        this.revokedDate = Instant.now();
    }
}
