package com.wojet.pmtool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.model.AppRole;
import com.wojet.pmtool.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole role);
}
