package com.wojet.pmtool.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.Task;

import jakarta.transaction.Transactional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
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

  @Query(value = """
        with recursive ancestors as (
          -- 1) seeds = the matching task ids we pass in
          select t.*
          from tasks t
          where t.project_id = :projectId and t.id = any(:matchIds)

          union all

          -- 2) add each parent (and parent of parent...)
          select p.*
          from tasks p
          join ancestors a on p.id = a.parent_id
          where p.project_id = :projectId
        )
        select distinct a.* from ancestors a
        -- Order by WBS if present, then by level as fallback
        order by coalesce(a.wbs, ''), a.level, a.id
      """, nativeQuery = true)
  List<Task> fetchMatchesPlusAncestors(Long projectId, Long[] matchIds);
}
