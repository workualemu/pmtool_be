package com.wojet.pmtool.database;

import static org.assertj.core.api.Assertions.assertThat;

import com.wojet.pmtool.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class FlywayBaselineIT extends PostgresIntegrationTest {
  @Autowired JdbcTemplate jdbc;

  @Test
  void createsTheIdentityAndTenancyTables() {
    List<String> names = jdbc.queryForList(
        "select table_name from information_schema.tables where table_schema = 'public'",
        String.class);
    assertThat(names).contains("users", "clients", "client_memberships",
        "client_invitations", "refresh_tokens", "audit_events");
  }
}
