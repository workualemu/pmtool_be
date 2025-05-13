package com.wojet.pmtool.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public ClientResponse getAllClients(Integer pageNumber, Integer pageSize, String sortBy, String sortDir){

        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Client> clientPage = clientRepository.findAll(pageable);

        List<Client> clients = clientPage.getContent();
        if(clients.isEmpty())
            throw new APIException("No client record available !!!");

        List<ClientDTO> clientDTOs = clients.stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();

        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setContent(clientDTOs);
        clientResponse.setPageNumber(clientPage.getNumber());
        clientResponse.setPageSize(clientPage.getSize());
        clientResponse.setTotalElements(clientPage.getTotalElements());
        clientResponse.setTotalPages(clientPage.getTotalPages());
        clientResponse.setLastPage(clientPage.isLast());

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

    public ClientDTO deleteClient(Long clientId){
        Client client = clientRepository.findById(clientId).orElseThrow(
            () -> new ResourceNotFoundException("Client", "clientId", clientId));
        clientRepository.deleteById(clientId);
        return modelMapper.map(client, ClientDTO.class);
    }

    public String deleteAllClients(){
        clientRepository.deleteAll();
        return "All clients deleted successfully !!!";
    }

}
