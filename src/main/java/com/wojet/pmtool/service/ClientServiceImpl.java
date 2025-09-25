package com.wojet.pmtool.service;

import static com.wojet.pmtool.util.AuditUtil.applyAuditOnCreate;
import static com.wojet.pmtool.util.AuditUtil.applyAuditOnUpdate;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Role;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.RoleRepository;
import com.wojet.pmtool.repository.UserRepository;
import com.wojet.pmtool.security.service.UserDetailsImpl;
import com.wojet.pmtool.util.AuthFacade;

@Service
public class ClientServiceImpl implements ClientService {

    private final JdbcTemplate jdbc;
    private final Environment env;
    private final PasswordEncoder encoder;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    AuthFacade auth;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditorAware<Long> auditorAware;

    @Autowired
    private ModelMapper modelMapper;

    public ClientServiceImpl(Environment env, JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.env = env;
        this.encoder = encoder;
    }

    ModelMapper mapper = new ModelMapper();

    /**
     * Get all clients with pagination and sorting
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of records per page
     * @param sortBy     the field to sort by
     * @param sortDir    the direction of sorting (asc or desc)
     * @return a paginated response containing client DTOs
     */
    public PagedResponse<ClientDTO> getAllClients(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Client> clientPage = clientRepository.findAll(pageable);

        List<Client> clients = clientPage.getContent();
        if (clients.isEmpty())
            throw new APIException("No client record available !!!");

        List<ClientDTO> clientDTOs = clients.stream()
                .map(client -> modelMapper.map(client, ClientDTO.class))
                .toList();

        PagedResponse<ClientDTO> clientResponse = new PagedResponse<>();
        clientResponse.setContent(clientDTOs);
        clientResponse.setPageNumber(clientPage.getNumber());
        clientResponse.setPageSize(clientPage.getSize());
        clientResponse.setTotalElements(clientPage.getTotalElements());
        clientResponse.setTotalPages(clientPage.getTotalPages());
        clientResponse.setLastPage(clientPage.isLast());

        return clientResponse;
    }

    /**
     * Get a client by its ID
     *
     * @param clientId the ID of the client to retrieve
     * @return the client DTO
     */
    public ClientDTO getClientById(Long clientId) {
        Optional<Client> optionalClient = clientRepository.getClientById(clientId);
        if (!optionalClient.isPresent())
            throw new APIException("Client with Client_ID '" + clientId + "'not found !!!");

        Client client = optionalClient.get();
        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);
        return clientDTO;
    }

    /**
     * Get a client by its name
     *
     * @param clientDTO the DTO of the client to retrieve
     * @return the client DTO
     */
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = modelMapper.map(clientDTO, Client.class);
        Client existingClient = clientRepository.findByName(client.getName());
        if (existingClient != null)
            throw new APIException("Client with name '" + client.getName() + "' already exists !!!");

        applyAuditOnCreate(client, auditorAware);

        Client savedClient = clientRepository.save(client);

        createClientAdminUser(client);

        return modelMapper.map(savedClient, ClientDTO.class);

    }

    /**
     * Update an existing client
     *
     * @param clientId  the ID of the client to update
     * @param clientDTO the DTO containing updated client information
     * @return the updated client DTO
     */
    public ClientDTO updateClient(Long clientId, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));

        if (clientRepository.existsByNameIgnoreCaseAndIdNot(clientDTO.getClientName(), clientId))
            throw new APIException("Client with name '" + clientDTO.getClientName() + "' already exists !!!");

        Client client = modelMapper.map(clientDTO, Client.class);
        client.setId(clientId);
        applyAuditOnUpdate(client, existingClient, auditorAware);

        Client savedClient = clientRepository.save(client);
        return modelMapper.map(savedClient, ClientDTO.class);
    }

    /**
     * Delete a client by its ID
     *
     * @param clientId the ID of the client to delete
     * @return the deleted client DTO
     */
    public ClientDTO deleteClient(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Client", "clientId", clientId));
        clientRepository.deleteById(clientId);
        return modelMapper.map(client, ClientDTO.class);
    }

    /**
     * Delete all clients
     *
     * @return a message indicating successful deletion
     */
    public String deleteAllClients() {
        clientRepository.deleteAll();
        return "All clients deleted successfully !!!";
    }

    @Transactional
    public void createClientAdminUser(Client client) {

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.currentUser();

        jdbc.update("""
                  INSERT INTO roles (name, description, client_id, created_at, updated_at, created_by, updated_by)
                  VALUES ('CLIENT_ADMIN', 'Client Administrator', ?, NOW(), NOW(), ?, ?)
                  ON CONFLICT (id) DO NOTHING
                """, client.getId(), userDetails.getId(), userDetails.getId());

        Role role = roleRepository.findByClientIdAndName(client.getId(), "CLIENT_ADMIN").orElseThrow(
                () -> new ResourceNotFoundException("Role", "name", "CLIENT_ADMIN"));

        jdbc.update("""
                INSERT INTO users (email, password, first_name, last_name, client_id)
                VALUES (?, '', ?, '', ?)
                ON CONFLICT (id) DO NOTHING
                """, client.getEmail(), client.getName(), client.getId());

        User clientUser = userRepository.findByEmail(client.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", client.getEmail()));

        clientUser.getRoles().add(role);
        var currentPwd = clientUser.getPassword();
        if (currentPwd == null || currentPwd.isBlank()) {
            String raw = env.getProperty("pmtool.client-admin-password", "SuperUser");
            clientUser.setPassword(encoder.encode(raw));
            userRepository.save(clientUser);
        }
    }

}
