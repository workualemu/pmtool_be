package com.wojet.pmtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wojet.pmtool.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
