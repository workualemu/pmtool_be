package com.wojet.pmtool.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService {
  private final SecretKey key;
  private final Duration lifetime;
  private final Clock clock;

  @org.springframework.beans.factory.annotation.Autowired
  public AccessTokenService(
      @Value("${pmtool.security.access-secret}") String secret,
      @Value("${pmtool.security.access-lifetime:PT15M}") Duration lifetime) {
    this(secret, lifetime, Clock.systemUTC());
  }

  AccessTokenService(String secret, Duration lifetime, Clock clock) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.lifetime = lifetime;
    this.clock = clock;
  }

  public String create(AuthenticatedUser user) {
    Instant now = clock.instant();
    return Jwts.builder()
        .subject(user.userId().toString())
        .claim("email", user.email())
        .claim("roles", user.platformRoles())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(lifetime)))
        .signWith(key)
        .compact();
  }

  public AuthenticatedUser parse(String token) {
    var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    @SuppressWarnings("unchecked")
    Set<String> roles = Set.copyOf((java.util.List<String>) claims.get("roles", java.util.List.class));
    return new AuthenticatedUser(
        UUID.fromString(claims.getSubject()), claims.get("email", String.class), roles);
  }
}
