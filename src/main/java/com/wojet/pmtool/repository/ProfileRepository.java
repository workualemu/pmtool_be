package com.wojet.pmtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    // Custom query methods can be defined here if needed

}
