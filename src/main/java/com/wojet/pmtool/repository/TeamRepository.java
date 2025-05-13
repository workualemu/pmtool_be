package com.wojet.pmtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Custom query methods can be defined here if needed

}
