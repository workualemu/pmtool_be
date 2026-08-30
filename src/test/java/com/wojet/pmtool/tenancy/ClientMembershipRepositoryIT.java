package com.wojet.pmtool.tenancy;

import static org.assertj.core.api.Assertions.assertThat;

import com.wojet.pmtool.identity.domain.User;
import com.wojet.pmtool.identity.domain.UserRepository;
import com.wojet.pmtool.support.PostgresIntegrationTest;
import com.wojet.pmtool.tenancy.domain.Client;
import com.wojet.pmtool.tenancy.domain.ClientMembership;
import com.wojet.pmtool.tenancy.domain.ClientMembershipRepository;
import com.wojet.pmtool.tenancy.domain.ClientRepository;
import com.wojet.pmtool.tenancy.domain.ClientRole;
import com.wojet.pmtool.tenancy.domain.MembershipStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ClientMembershipRepositoryIT extends PostgresIntegrationTest {
  @Autowired UserRepository users;
  @Autowired ClientRepository clients;
  @Autowired ClientMembershipRepository memberships;

  @Test
  void oneIdentityCanHaveMembershipsInTwoClients() {
    User user = users.save(User.create(" Alex@Example.ORG ", "hash", "Alex", "Morgan"));
    Client first = clients.save(Client.create("Alpha", " Alpha "));
    Client second = clients.save(Client.create("Beta", "beta"));
    memberships.save(ClientMembership.active(first, user, ClientRole.CLIENT_ADMIN));
    memberships.save(ClientMembership.active(second, user, ClientRole.MEMBER));

    assertThat(memberships.findActiveByUserId(user.getId()))
        .extracting(membership -> membership.getClient().getId())
        .containsExactlyInAnyOrder(first.getId(), second.getId());
    assertThat(users.findByEmail("alex@example.org")).contains(user);
    assertThat(clients.findBySlug("alpha")).contains(first);
  }

  @Test
  void suspendedMembershipIsNotActive() {
    User user = users.save(User.create("suspended@example.org", "hash", "Sam", "Lee"));
    Client client = clients.save(Client.create("Suspended membership client", "suspended-member"));
    ClientMembership membership = memberships.save(
        ClientMembership.create(client, user, ClientRole.GUEST, MembershipStatus.SUSPENDED));

    assertThat(memberships.findActive(client.getId(), user.getId())).isEmpty();
    assertThat(memberships.findActiveByUserId(user.getId())).isEmpty();
    assertThat(membership.getStatus()).isEqualTo(MembershipStatus.SUSPENDED);
  }
}
