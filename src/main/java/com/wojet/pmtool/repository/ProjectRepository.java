package com.wojet.pmtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Custom query methods can be defined here if needed

}
