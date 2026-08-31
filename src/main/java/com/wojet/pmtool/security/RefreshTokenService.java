package com.wojet.pmtool.security;

import com.wojet.pmtool.identity.domain.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
  public record Issued(String raw, User user) {}
  private final RefreshTokenRepository tokens;
  private final Duration lifetime;
  private final SecureRandom random = new SecureRandom();

  public RefreshTokenService(
      RefreshTokenRepository tokens,
      @Value("${pmtool.security.refresh-lifetime:PT336H}") Duration lifetime) {
    this.tokens = tokens;
    this.lifetime = lifetime;
  }

  @Transactional
  public Issued issue(User user) {
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    tokens.save(RefreshToken.create(user, hash(raw), Instant.now().plus(lifetime)));
    return new Issued(raw, user);
  }

  @Transactional
  public Issued rotate(String raw) {
    Instant now = Instant.now();
    RefreshToken current = tokens.findByTokenHash(hash(raw))
        .filter(token -> token.isAvailable(now))
        .orElseThrow(InvalidRefreshTokenException::new);
    current.revoke(now);
    return issue(current.getUser());
  }

  @Transactional
  public void revoke(String raw) {
    tokens.findByTokenHash(hash(raw)).ifPresent(token -> token.revoke(Instant.now()));
  }

  private static String hash(String raw) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256")
          .digest(raw.getBytes(StandardCharsets.UTF_8));
      return java.util.HexFormat.of().formatHex(digest);
    } catch (java.security.NoSuchAlgorithmException impossible) {
      throw new IllegalStateException(impossible);
    }
  }

  @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
  public static class InvalidRefreshTokenException extends RuntimeException {}
}
