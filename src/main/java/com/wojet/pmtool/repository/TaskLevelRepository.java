package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.TaskLevel;

import jakarta.transaction.Transactional;

public interface TaskLevelRepository extends JpaRepository<TaskLevel, Long> {
  TaskLevel findByProjectIdAndLevel(Long projectId, Integer level);

  Page<TaskLevel> findByProjectId(Long ProjectId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("delete from TaskLevel t where t.project.id = :projectId")
  void deleteByProjectId(@Param("projectId") Long projectId);
}
