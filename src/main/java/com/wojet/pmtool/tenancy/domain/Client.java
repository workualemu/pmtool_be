package com.wojet.pmtool.tenancy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {
  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true, length = 100)
  private String slug;

  @Column(nullable = false, length = 32)
  private String status;

  @Version
  private long version;

  Client() {}

  private Client(String name, String slug) {
    this.id = UUID.randomUUID();
    this.name = Objects.requireNonNull(name).trim();
    this.slug = normalizeSlug(slug);
    this.status = "ACTIVE";
  }

  public static Client create(String name, String slug) {
    return new Client(name, slug);
  }

  private static String normalizeSlug(String slug) {
    return Objects.requireNonNull(slug).trim().toLowerCase(Locale.ROOT);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSlug() {
    return slug;
  }

  public boolean isActive() {
    return "ACTIVE".equals(status);
  }
}
