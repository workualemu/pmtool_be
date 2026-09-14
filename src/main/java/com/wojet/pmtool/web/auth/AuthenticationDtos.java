package com.wojet.pmtool.web.auth;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AuthenticationDtos {
  private AuthenticationDtos() {}

  public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
  public record ClientSummary(UUID id, String name, String slug, String role) {}
  public record CurrentUserResponse(
      UUID id, String email, String firstName, String lastName,
      Set<String> platformRoles, List<ClientSummary> clients) {}
}
