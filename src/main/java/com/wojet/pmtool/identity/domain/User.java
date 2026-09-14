package com.wojet.pmtool.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id
  private UUID id;

  @Column(nullable = false, length = 320)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(nullable = false)
  private boolean enabled;

  @Version
  private long version;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "platform_user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role", nullable = false, length = 64)
  private Set<String> platformRoles = new LinkedHashSet<>();

  User() {}

  private User(String email, String passwordHash, String firstName, String lastName) {
    this.id = UUID.randomUUID();
    this.email = normalizeEmail(email);
    this.passwordHash = Objects.requireNonNull(passwordHash);
    this.firstName = Objects.requireNonNull(firstName).trim();
    this.lastName = Objects.requireNonNull(lastName).trim();
    this.enabled = true;
  }

  public static User create(
      String email, String passwordHash, String firstName, String lastName) {
    return new User(email, passwordHash, firstName, lastName);
  }

  private static String normalizeEmail(String email) {
    return Objects.requireNonNull(email).trim().toLowerCase(Locale.ROOT);
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Set<String> getPlatformRoles() {
    return Set.copyOf(platformRoles);
  }
}
