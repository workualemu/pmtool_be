package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ClientResponse;

public interface ClientService {

    ClientResponse getAllClients(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    ClientDTO getClientById(Long clientId);
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    ClientDTO deleteClient(Long id);
    String deleteAllClients();
}
