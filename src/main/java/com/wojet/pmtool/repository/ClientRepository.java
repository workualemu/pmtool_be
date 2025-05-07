package com.wojet.pmtool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wojet.pmtool.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Client findByName(String name);
    Optional<Client> getClientById(Long clientId);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
