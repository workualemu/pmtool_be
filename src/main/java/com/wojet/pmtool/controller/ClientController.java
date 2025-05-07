package com.wojet.pmtool.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.wojet.pmtool.service.ClientService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wojet.pmtool.model.Client;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/admin")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return clients;
    }

    @GetMapping("/clients/{clientId}")
    public Client getClient(@PathVariable Long clientId) {
        Optional<Client> optionalClient = clientService.getClientById(clientId);
        if(optionalClient.isPresent()){
            return optionalClient.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }

    @PostMapping("/clients")
    public Client createClient(@Valid @RequestBody Client client) {
        return clientService.createClient(client);
    }

    @PutMapping("/clients/{clientId}")
    public Client updateClient(@PathVariable Long clientId, @RequestBody Client client) {
        return clientService.updateClient(clientId, client);
    }
    
    @DeleteMapping("/clients/{clientId}")
    public String updateClient(@PathVariable Long clientId) {
        try {
            clientService.deleteClient(clientId);
            return "Client deleted successfully";
       } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found", e);
        }
        
    }
}
