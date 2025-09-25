package com.wojet.pmtool.service.implementation;

import java.util.List;
import java.util.function.Consumer;

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
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.Role;
import com.wojet.pmtool.model.Tag;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.RoleDTO;
import com.wojet.pmtool.payload.TagDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.RoleRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.RoleService;

@Service
public class RoleServiceImpl extends GenericCrudService<Role, RoleDTO, RoleRepository>
    implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all roles by project with pagination and sorting
   */
  public PagedResponse<RoleDTO> getRolesByClient(Long clientId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<Role> page = roleRepository.findByClientId(clientId, pageable);

    List<Role> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<RoleDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<RoleDTO> response = new PagedResponse<>();
    response.setContent(dtos);
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLastPage(page.isLast());

    return response;
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public RoleDTO createRole(Long clientId, RoleDTO roleDTO) {
    Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

    Consumer<Role> associations = role -> {
      role.setClient(client);
    };

    return createWithAssociations(roleDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public RoleDTO updateRole(Long roleId, RoleDTO roleDTO) {
    Role existingRole = roleRepository.findById(roleId)
        .orElseThrow(() -> new ResourceNotFoundException("role", "id", roleId));

    Long clientId = roleDTO.getClientId() == null ? existingRole.getClient().getId() : roleDTO.getClientId();

    Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

    Consumer<Role> associations = role -> {
      role.setClient(client);
    };

    return updateWithAssociations(roleId, roleDTO, associations);
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected Role mapToEntity(RoleDTO dto) {
    return modelMapper.map(dto, Role.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected RoleDTO mapToDTO(Role entity) {
    return modelMapper.map(entity, RoleDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(Role entity) {
    return roleRepository.findByClientIdAndName(entity.getClient().getId(), entity.getName()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(RoleDTO dto, Long id) {
    Role existing = roleRepository.findByClientIdAndName(dto.getClientId(), dto.getName()).orElseThrow(
      () -> new ResourceNotFoundException("Role", "id", id));
    return existing != null && !existing.getId().equals(id);
  }

}
