package com.wojet.pmtool.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ClientResponse;
import com.wojet.pmtool.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ClientResponse getAllClients(){

        List<Client> clients = clientRepository.findAll();
        if(clients.isEmpty())
            throw new APIException("No client record available !!!");

        List<ClientDTO> clientDTOs = clients.stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();

        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setContent(clientDTOs);

        return clientResponse;
    }

    public ClientDTO getClientById(Long clientId){
        Optional<Client> optionalClient = clientRepository.getClientById(clientId);
        if(!optionalClient.isPresent())
            throw new APIException("Client with Client_ID '" + clientId + "'not found !!!");

        Client client = optionalClient.get();
        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);
        return clientDTO;
    }

    public ClientDTO createClient(ClientDTO clientDTO){
        Client client = modelMapper.map(clientDTO, Client.class);
        Client existingClient = clientRepository.findByName(client.getName());
        if(existingClient != null) 
            throw new APIException("Client with name '" + client.getName() + "' already exists !!!");

        Client savedClient = clientRepository.save(client);
        return modelMapper.map(savedClient, ClientDTO.class);
    }

    public ClientDTO updateClient(Long clientId, ClientDTO clientDTO){
        Client client = modelMapper.map(clientDTO, Client.class);
        if (!clientRepository.existsById(clientId)) 
            throw new ResourceNotFoundException("Client", "clientId", clientId);

        if (clientRepository.existsByNameIgnoreCaseAndIdNot(client.getName(), clientId)) 
            throw new APIException("Client with name '" + client.getName() + "' already exists !!!");

        client.setId(clientId);
        Client savedClient = clientRepository.save(client);

        return modelMapper.map(savedClient, ClientDTO.class);
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
