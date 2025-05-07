package com.wojet.pmtool.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients(){

        List<Client> clients = clientRepository.findAll();
        if(clients.isEmpty())
            throw new APIException("No client record available !!!");

        return clients;
    }

    public Client getClientById(Long clientId){
        Optional<Client> optionalClient = clientRepository.getClientById(clientId);
        if(!optionalClient.isPresent())
            throw new APIException("Client with Client_ID '" + clientId + "'not found !!!");

        return optionalClient.get();
    }

    public Client createClient(Client client){
        Client existingClient = clientRepository.findByName(client.getName());
        if(existingClient != null) 
            throw new APIException("Client with name '" + client.getName() + "' already exists !!!");

        return clientRepository.save(client);
    }

    public Client updateClient(Long clientId, Client client){
        if (!clientRepository.existsById(clientId)) 
            throw new ResourceNotFoundException("Client", "clientId", clientId);

        if (clientRepository.existsByNameIgnoreCaseAndIdNot(client.getName(), clientId)) 
            throw new APIException("Client with name '" + client.getName() + "' already exists !!!");

        client.setId(clientId);
        return clientRepository.save(client);
    }

    public String deleteClient(Long id){
        if (!clientRepository.existsById(id)) 
            throw new ResourceNotFoundException("Client", "clientId", id);

        clientRepository.deleteById(id);
        return "Client with id '" + id + "' deleted successfully !!!";
    }

    public String deleteAllClients(){
        clientRepository.deleteAll();
        return "All clients deleted successfully !!!";
    }

}
