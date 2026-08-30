package com.wojet.pmtool.tenancy.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientMembershipRepository extends JpaRepository<ClientMembership, UUID> {
  Optional<ClientMembership> findByClientIdAndUserIdAndStatus(
      UUID clientId, UUID userId, MembershipStatus status);

  default Optional<ClientMembership> findActive(UUID clientId, UUID userId) {
    return findByClientIdAndUserIdAndStatus(clientId, userId, MembershipStatus.ACTIVE);
  }

  @Query("""
      select m from ClientMembership m join fetch m.client
      where m.user.id = :userId
        and m.status = com.wojet.pmtool.tenancy.domain.MembershipStatus.ACTIVE
      order by m.client.name
      """)
  List<ClientMembership> findActiveByUserId(@Param("userId") UUID userId);
}
