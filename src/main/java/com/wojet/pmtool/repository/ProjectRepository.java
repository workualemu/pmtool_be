package com.wojet.pmtool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wojet.pmtool.model.Project;

import jakarta.transaction.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByTitle(String name); 
    Page<Project> findByClientId(Long clientId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Project p where p.client.id = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);
}
