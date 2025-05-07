package com.wojet.pmtool.service;

import java.util.List;
import java.util.Optional;
import com.wojet.pmtool.model.Client;

public interface ClientService {

    List<Client> getAllClients();
    Optional<Client> getClientById(Long id);
    Client createClient(Client client);
    Client updateClient(Long id, Client client);
    void deleteClient(Long id);
}
