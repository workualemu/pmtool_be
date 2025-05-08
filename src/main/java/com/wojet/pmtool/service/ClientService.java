package com.wojet.pmtool.service;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ClientResponse;

public interface ClientService {

    ClientResponse getAllClients();
    ClientDTO getClientById(Long clientId);
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    String deleteClient(Long id);
    String deleteAllClients();
}
