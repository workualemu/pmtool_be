package com.wojet.pmtool.web.auth;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wojet.pmtool.identity.domain.User;
import com.wojet.pmtool.identity.domain.UserRepository;
import com.wojet.pmtool.support.PostgresIntegrationTest;
import com.wojet.pmtool.tenancy.domain.Client;
import com.wojet.pmtool.tenancy.domain.ClientMembership;
import com.wojet.pmtool.tenancy.domain.ClientMembershipRepository;
import com.wojet.pmtool.tenancy.domain.ClientRepository;
import com.wojet.pmtool.tenancy.domain.ClientRole;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
class AuthenticationControllerIT extends PostgresIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired UserRepository users;
  @Autowired ClientRepository clients;
  @Autowired ClientMembershipRepository memberships;
  @Autowired PasswordEncoder passwords;

  @BeforeEach
  void seedUser() {
    User user = users.save(User.create(
        "alex@example.org", passwords.encode("correct horse"), "Alex", "Morgan"));
    Client client = clients.save(Client.create("Alpha", "alpha"));
    memberships.save(ClientMembership.active(client, user, ClientRole.CLIENT_ADMIN));
  }

  @Test
  void loginRefreshMeAndLogoutRotateHttpOnlyCookies() throws Exception {
    MvcResult login = mvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\" ALEX@example.org \",\"password\":\"correct horse\"}"))
        .andExpect(status().isOk())
        .andExpect(cookie().httpOnly("pm_access", true))
        .andExpect(cookie().httpOnly("pm_refresh", true))
        .andExpect(jsonPath("$.email").value("alex@example.org"))
        .andExpect(jsonPath("$.clients[0].slug").value("alpha"))
        .andReturn();

    Cookie access = login.getResponse().getCookie("pm_access");
    Cookie refresh = login.getResponse().getCookie("pm_refresh");
    mvc.perform(get("/api/v1/auth/me").cookie(access))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Alex"));

    MvcResult refreshed = mvc.perform(post("/api/v1/auth/refresh").cookie(refresh))
        .andExpect(status().isOk())
        .andExpect(cookie().value("pm_refresh", not(refresh.getValue())))
        .andReturn();

    mvc.perform(post("/api/v1/auth/refresh").cookie(refresh))
        .andExpect(status().isUnauthorized());

    mvc.perform(post("/api/v1/auth/logout")
            .cookie(refreshed.getResponse().getCookie("pm_refresh")))
        .andExpect(status().isNoContent())
        .andExpect(cookie().maxAge("pm_access", 0))
        .andExpect(cookie().maxAge("pm_refresh", 0));
  }

  @Test
  void rejectsInvalidCredentialsAndUnauthenticatedMe() throws Exception {
    mvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"alex@example.org\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(get("/api/v1/auth/me"))
        .andExpect(status().isUnauthorized());
  }
}
