package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.Task;

import jakarta.transaction.Transactional;

public interface TaskRepository extends JpaRepository<Task, Long> {
  Task findByProjectIdAndTitle(Long projectId, String title);

  Page<Task> findByProjectId(Long ProjectId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("delete from Task t where t.project.id = :projectId")
  void deleteByProjectId(@Param("projectId") Long projectId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE tasks SET path = CAST(:path AS ltree) WHERE id = :id", nativeQuery = true)
  void updatePath(@Param("id") Long id, @Param("path") String path);
}
