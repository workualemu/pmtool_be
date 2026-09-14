package com.wojet.pmtool.security;

import com.wojet.pmtool.identity.domain.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  @Id private UUID id;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  @Column(name = "token_hash", nullable = false, unique = true, length = 128)
  private String tokenHash;
  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;
  @Column(name = "revoked_at")
  private Instant revokedAt;
  @Version private long version;

  RefreshToken() {}

  private RefreshToken(User user, String tokenHash, Instant expiresAt) {
    this.id = UUID.randomUUID();
    this.user = user;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
  }

  public static RefreshToken create(User user, String tokenHash, Instant expiresAt) {
    return new RefreshToken(user, tokenHash, expiresAt);
  }

  public User getUser() { return user; }
  public boolean isAvailable(Instant now) { return revokedAt == null && expiresAt.isAfter(now); }
  public void revoke(Instant now) { revokedAt = now; }
}
