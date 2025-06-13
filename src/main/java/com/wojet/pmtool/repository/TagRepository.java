package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.Tag;

import jakarta.transaction.Transactional;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Tag findByProjectIdAndLabel(Long projectId, String label);

  Page<Tag> findByProjectId(Long ProjectId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("delete from Tag t where t.project.id = :projectId")
  void deleteByProjectId(@Param("projectId") Long projectId);
}
