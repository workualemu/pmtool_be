package com.wojet.pmtool.tenancy.domain;

import com.wojet.pmtool.identity.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "client_memberships")
public class ClientMembership {
  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ClientRole role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private MembershipStatus status;

  @Version
  private long version;

  ClientMembership() {}

  private ClientMembership(
      Client client, User user, ClientRole role, MembershipStatus status) {
    this.id = UUID.randomUUID();
    this.client = Objects.requireNonNull(client);
    this.user = Objects.requireNonNull(user);
    this.role = Objects.requireNonNull(role);
    this.status = Objects.requireNonNull(status);
  }

  public static ClientMembership active(Client client, User user, ClientRole role) {
    return create(client, user, role, MembershipStatus.ACTIVE);
  }

  public static ClientMembership create(
      Client client, User user, ClientRole role, MembershipStatus status) {
    return new ClientMembership(client, user, role, status);
  }

  public UUID getId() {
    return id;
  }

  public Client getClient() {
    return client;
  }

  public User getUser() {
    return user;
  }

  public ClientRole getRole() {
    return role;
  }

  public MembershipStatus getStatus() {
    return status;
  }
}
