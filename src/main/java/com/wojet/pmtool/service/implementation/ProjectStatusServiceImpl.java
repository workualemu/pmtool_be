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
import com.wojet.pmtool.model.ProjectStatus;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.ProjectStatusDTO;
import com.wojet.pmtool.payload.TaskStatusDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.ProjectStatusRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.ProjectStatusService;

@Service
public class ProjectStatusServiceImpl
    extends GenericCrudService<ProjectStatus, ProjectStatusDTO, ProjectStatusRepository>
    implements ProjectStatusService {

  @Autowired
  private ProjectStatusRepository projectStatusRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all ProjectStatuss by project with pagination and sorting
   */
  public PagedResponse<ProjectStatusDTO> getByClient(Long clientId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<ProjectStatus> page = projectStatusRepository.findByClientId(clientId, pageable);

    List<ProjectStatus> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<ProjectStatusDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<ProjectStatusDTO> response = new PagedResponse<>();
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
  public ProjectStatusDTO createProjectStatus(Long clientId, ProjectStatusDTO projectStatusDTO) {

    Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

    Consumer<ProjectStatus> associations = ProjectStatus -> {
      ProjectStatus.setClient(client);
    };

    return createWithAssociations(projectStatusDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public ProjectStatusDTO updateProjectStatus(Long projectStatusId, ProjectStatusDTO projectStatusDTO) {
    ProjectStatus existingProjectStatus = projectStatusRepository.findById(projectStatusId)
        .orElseThrow(() -> new ResourceNotFoundException("ProjectStatus", "id", projectStatusId));

    Long clientId = projectStatusDTO.getClientId() == null ? existingProjectStatus.getClient().getId()
      : projectStatusDTO.getClientId();

    Client client = clientRepository.findById(
        clientId)
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

    Consumer<ProjectStatus> associations = ProjectStatus -> {
      ProjectStatus.setClient(client);
    };

    return updateWithAssociations(projectStatusId, projectStatusDTO, associations);
  }

  @Override
  public String deleteByClientId(Long clientId) {
    projectStatusRepository.deleteByClientId(clientId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected ProjectStatus mapToEntity(ProjectStatusDTO dto) {
    return modelMapper.map(dto, ProjectStatus.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected ProjectStatusDTO mapToDTO(ProjectStatus entity) {
    return modelMapper.map(entity, ProjectStatusDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(ProjectStatus entity) {
    return projectStatusRepository.findByClientIdAndValue(entity.getClient().getId(), entity.getValue()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(ProjectStatusDTO dto, Long id) {
    ProjectStatus existing = projectStatusRepository.findByClientIdAndValue(dto.getClientId(), dto.getValue());
    return existing != null && !existing.getId().equals(id);
  }

}
