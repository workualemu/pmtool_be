package com.wojet.pmtool.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AccessTokenFilter extends OncePerRequestFilter {
  private final AccessTokenService tokens;

  public AccessTokenFilter(AccessTokenService tokens) { this.tokens = tokens; }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> "pm_access".equals(cookie.getName()))
          .findFirst()
          .ifPresent(this::authenticate);
    }
    chain.doFilter(request, response);
  }

  private void authenticate(Cookie cookie) {
    try {
      AuthenticatedUser principal = tokens.parse(cookie.getValue());
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    } catch (JwtException | IllegalArgumentException ignored) {
      SecurityContextHolder.clearContext();
    }
  }
}
