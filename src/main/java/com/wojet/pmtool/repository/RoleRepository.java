package com.wojet.pmtool.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    Page<Role> findByClientId(Long clientId, Pageable pageable);

    Optional<Role> findByClientIdAndName(Long clientId, String name);
}
