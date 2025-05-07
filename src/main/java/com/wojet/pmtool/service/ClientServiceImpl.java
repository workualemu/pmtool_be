package com.wojet.pmtool.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id){
        return clientRepository.findById(id);
    }

    public Client createClient(Client client){
        return clientRepository.save(client);
    }
    public Client updateClient(Long id, Client client){
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", "clientId", id);
        }
        client.setId(id);
        return clientRepository.save(client);
    }

    public void deleteClient(Long id){
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", "clientId", id);
        }
        clientRepository.deleteById(id);
    }

}
