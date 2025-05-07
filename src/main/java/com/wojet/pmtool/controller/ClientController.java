package com.wojet.pmtool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/clients/{clientId}")
    public Client getClient(@PathVariable Long clientId) {
        return clientService.getClientById(clientId);
    }

    @PostMapping("/clients")
    public Client createClient(@Valid @RequestBody Client client) {
        return clientService.createClient(client);
    }

    @PutMapping("/clients/{clientId}")
    public Client updateClient(@Valid @PathVariable Long clientId, @RequestBody Client client) {
        return clientService.updateClient(clientId, client);
    }
    
    @DeleteMapping("/clients/{clientId}")
    public String deleteClient(@PathVariable Long clientId) {
        return clientService.deleteClient(clientId);
    }

    @DeleteMapping("/clients/all")
    public String deleteAllClients() {
        return clientService.deleteAllClients();
    }
}
