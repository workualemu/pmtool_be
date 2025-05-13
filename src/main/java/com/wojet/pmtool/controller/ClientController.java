package com.wojet.pmtool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ClientResponse;
import com.wojet.pmtool.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/clients")
    public ClientResponse getAllClients(
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
        @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
        @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return clientService.getAllClients(pageNumber, pageSize, sortBy, sortDir);
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long clientId) {
        ClientDTO clinetDTO =  clientService.getClientById(clientId);
        return new ResponseEntity<>(clinetDTO, HttpStatus.OK);
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.OK);
    }

    @PutMapping("/clients/{clientId}")
    public ResponseEntity<ClientDTO> updateClient(@Valid @PathVariable Long clientId, @RequestBody ClientDTO clientDTO) {
        ClientDTO updatedClient = clientService.updateClient(clientId, clientDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }
    
    @DeleteMapping("/clients/{clientId}")
    public ResponseEntity<ClientDTO> deleteClient(@PathVariable Long clientId) {
        ClientDTO deletedClient =  clientService.deleteClient(clientId);
        return new ResponseEntity<>(deletedClient, HttpStatus.OK);
    }

    @DeleteMapping("/clients/all")
    public String deleteAllClients() {
        return clientService.deleteAllClients();
    }
}
