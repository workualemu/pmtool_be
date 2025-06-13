package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.TaskPriority;

import jakarta.transaction.Transactional;

public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {
  TaskPriority findByProjectIdAndValue(Long projectId, String value);

  Page<TaskPriority> findByProjectId(Long ProjectId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("delete from TaskPriority t where t.project.id = :projectId")
  void deleteByProjectId(@Param("projectId") Long projectId);
}
