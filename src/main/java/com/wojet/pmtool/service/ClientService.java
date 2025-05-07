package com.wojet.pmtool.service;

import java.util.List;
import com.wojet.pmtool.model.Client;

public interface ClientService {

    List<Client> getAllClients();
    Client getClientById(Long clientId);
    Client createClient(Client client);
    Client updateClient(Long id, Client client);
    String deleteClient(Long id);
    String deleteAllClients();
}
