package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.ProjectStatus;

import jakarta.transaction.Transactional;

public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {
  ProjectStatus findByClientIdAndValue(Long clientId, String value);

  Page<ProjectStatus> findByClientId(Long clientId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("delete from ProjectStatus t where t.client.id = :clientId")
  void deleteByClientId(@Param("clientId") Long clientId);
}
