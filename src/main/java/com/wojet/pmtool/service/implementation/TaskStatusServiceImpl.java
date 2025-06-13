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
import com.wojet.pmtool.model.TaskStatus;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskStatusDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.TaskStatusRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.TaskStatusService;

@Service
public class TaskStatusServiceImpl extends GenericCrudService<TaskStatus, TaskStatusDTO, TaskStatusRepository>
    implements TaskStatusService {

  @Autowired
  private TaskStatusRepository taskStatusRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all TaskStatuss by project with pagination and sorting
   */
  public PagedResponse<TaskStatusDTO> getByProject(Long projectId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<TaskStatus> page = taskStatusRepository.findByProjectId(projectId, pageable);

    List<TaskStatus> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<TaskStatusDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<TaskStatusDTO> response = new PagedResponse<>();
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
  public TaskStatusDTO createTaskStatus(Long projectId, TaskStatusDTO TaskStatusDTO) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", project.getClient().getId()));

    Consumer<TaskStatus> associations = TaskStatus -> {
      TaskStatus.setProject(project);
      TaskStatus.setClient(client);
    };

    return createWithAssociations(TaskStatusDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TaskStatusDTO updateTaskStatus(Long TaskStatusId, TaskStatusDTO TaskStatusDTO) {
    TaskStatus existingTaskStatus = taskStatusRepository.findById(TaskStatusId)
        .orElseThrow(() -> new ResourceNotFoundException("TaskStatus", "id", TaskStatusId));

    Long projectId = TaskStatusDTO.getProjectId() == null ? existingTaskStatus.getProject().getId()
        : TaskStatusDTO.getProjectId();
    Project project = projectRepository.findById(
        projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<TaskStatus> associations = TaskStatus -> {
      TaskStatus.setProject(project);
      TaskStatus.setClient(client);
    };

    return updateWithAssociations(TaskStatusId, TaskStatusDTO, associations);
  }

  @Override
  public String deleteByProjectId(Long projectId) {
    taskStatusRepository.deleteByProjectId(projectId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected TaskStatus mapToEntity(TaskStatusDTO dto) {
    return modelMapper.map(dto, TaskStatus.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected TaskStatusDTO mapToDTO(TaskStatus entity) {
    return modelMapper.map(entity, TaskStatusDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(TaskStatus entity) {
    return taskStatusRepository.findByProjectIdAndValue(entity.getProject().getId(), entity.getValue()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(TaskStatusDTO dto, Long id) {
    TaskStatus existing = taskStatusRepository.findByProjectIdAndValue(dto.getProjectId(), dto.getValue());
    return existing != null && !existing.getId().equals(id);
  }

}
