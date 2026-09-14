package com.wojet.pmtool.web.auth;

import static com.wojet.pmtool.web.auth.AuthenticationDtos.*;

import com.wojet.pmtool.identity.domain.User;
import com.wojet.pmtool.identity.domain.UserRepository;
import com.wojet.pmtool.security.AccessTokenService;
import com.wojet.pmtool.security.AuthenticatedUser;
import com.wojet.pmtool.security.RefreshTokenService;
import com.wojet.pmtool.tenancy.domain.ClientMembershipRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
  private final UserRepository users;
  private final ClientMembershipRepository memberships;
  private final PasswordEncoder passwords;
  private final AccessTokenService accessTokens;
  private final RefreshTokenService refreshTokens;
  private final boolean secureCookies;

  public AuthenticationController(
      UserRepository users, ClientMembershipRepository memberships,
      PasswordEncoder passwords, AccessTokenService accessTokens,
      RefreshTokenService refreshTokens,
      @Value("${pmtool.security.secure-cookies:true}") boolean secureCookies) {
    this.users = users;
    this.memberships = memberships;
    this.passwords = passwords;
    this.accessTokens = accessTokens;
    this.refreshTokens = refreshTokens;
    this.secureCookies = secureCookies;
  }

  @PostMapping("/login")
  @Transactional
  public CurrentUserResponse login(
      @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
    String email = request.email().trim().toLowerCase(Locale.ROOT);
    if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
    }
    User user = users.findByEmail(email)
        .filter(User::isEnabled)
        .filter(candidate -> passwords.matches(request.password(), candidate.getPasswordHash()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    setSession(response, user, refreshTokens.issue(user).raw());
    return response(user);
  }

  @PostMapping("/refresh")
  @Transactional
  public CurrentUserResponse refresh(HttpServletRequest request, HttpServletResponse response) {
    var issued = refreshTokens.rotate(cookie(request, "pm_refresh"));
    setSession(response, issued.user(), issued.raw());
    return response(issued.user());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String raw = optionalCookie(request, "pm_refresh");
    if (raw != null) refreshTokens.revoke(raw);
    addCookie(response, "pm_access", "", Duration.ZERO);
    addCookie(response, "pm_refresh", "", Duration.ZERO);
  }

  @GetMapping("/me")
  @Transactional(readOnly = true)
  public CurrentUserResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
    User user = users.findById(principal.userId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return response(user);
  }

  private CurrentUserResponse response(User user) {
    var clients = memberships.findActiveByUserId(user.getId()).stream()
        .map(m -> new ClientSummary(m.getClient().getId(), m.getClient().getName(),
            m.getClient().getSlug(), m.getRole().name()))
        .toList();
    return new CurrentUserResponse(user.getId(), user.getEmail(), user.getFirstName(),
        user.getLastName(), user.getPlatformRoles(), clients);
  }

  private void setSession(HttpServletResponse response, User user, String refresh) {
    addCookie(response, "pm_access", accessTokens.create(new AuthenticatedUser(
        user.getId(), user.getEmail(), user.getPlatformRoles())), Duration.ofMinutes(15));
    addCookie(response, "pm_refresh", refresh, Duration.ofDays(14));
  }

  private void addCookie(HttpServletResponse response, String name, String value, Duration age) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(secureCookies);
    cookie.setPath("/");
    cookie.setMaxAge(Math.toIntExact(age.toSeconds()));
    cookie.setAttribute("SameSite", "Lax");
    response.addCookie(cookie);
  }

  private static String cookie(HttpServletRequest request, String name) {
    String value = optionalCookie(request, name);
    if (value == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    return value;
  }

  private static String optionalCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return null;
    return Arrays.stream(request.getCookies()).filter(c -> name.equals(c.getName()))
        .map(Cookie::getValue).findFirst().orElse(null);
  }
}
