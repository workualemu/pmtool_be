// src/main/java/com/wojet/pmtool/bootstrap/SystemBootstrapRunner.java
package com.wojet.pmtool.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.UserRepository;

@Component
@Order(100)
public class SystemBootstrapRunner implements ApplicationRunner {

  private static final long SYSTEM_ID = 0L;
  private static final String SYSTEM_EMAIL = "system@internal";

  private final JdbcTemplate jdbc; // <— use SQL upsert
  private final ClientRepository clients;
  private final UserRepository users;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;
  private final Environment env;

  public SystemBootstrapRunner(JdbcTemplate jdbc,
      ClientRepository clients,
      UserRepository users,
      org.springframework.security.crypto.password.PasswordEncoder encoder,
      Environment env) {
    this.jdbc = jdbc;
    this.clients = clients;
    this.users = users;
    this.encoder = encoder;
    this.env = env;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    // 1) Upsert System Client (id=0) via SQL

    // ROLE_SYS_ADMIN,
    // ROLE_CLIENT_ADMIN,

    // // Default roles assigned to users
    // jdbc.update("""
    //       INSERT INTO roles (role_name, description, first_name, last_name, client_id)
    //       VALUES (0, ?, '', 'System', 'User', 0)
    //       ON CONFLICT (id) DO NOTHING
    //     """, SYSTEM_EMAIL);


    jdbc.update("""
          INSERT INTO clients (id, name, description, created_at, updated_at, created_by, updated_by)
          VALUES (0, 'System Client', 'Internal tenant for system-owned records', NOW(), NOW(), NULL, NULL)
          ON CONFLICT (id) DO NOTHING
        """);

    // 2) Upsert System User (id=0) via SQL (password will be (re)hashed below if
    // needed)
    jdbc.update("""
          INSERT INTO users (id, email, password, first_name, last_name, client_id)
          VALUES (0, ?, '', 'System', 'User', 0)
          ON CONFLICT (id) DO NOTHING
        """, SYSTEM_EMAIL);

    // 3) Re-hydrate with JPA
    Client sysClient = clients.findById(SYSTEM_ID).orElseThrow();
    User sysUser = users.findById(SYSTEM_ID).orElseGet(() -> {
      // If row inserted by SQL but repo didn't see it (rare), build a managed entity:
      User u = new User();
      u.setId(SYSTEM_ID);
      u.setEmail(SYSTEM_EMAIL);
      u.setClient(sysClient);
      return users.save(u);
    });

    // 4) Ensure password is set (only once)
    var currentPwd = sysUser.getPassword();
    if (currentPwd == null || currentPwd.isBlank()) {
      String raw = env.getProperty("pmtool.system-user-password", "SystemUser");
      sysUser.setPassword(encoder.encode(raw));
      users.save(sysUser); // this is an UPDATE now (managed entity)
    }

    // 5) Backfill audit ids on client 0 if missing
    if (sysClient.getCreatedById() == null || sysClient.getUpdatedById() == null) {
      sysClient.setCreatedById(SYSTEM_ID);
      sysClient.setUpdatedById(SYSTEM_ID);
      clients.save(sysClient); // UPDATE
    }
  }
}
