package com.wojet.pmtool.tenancy.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  Optional<Client> findBySlug(String slug);
}
