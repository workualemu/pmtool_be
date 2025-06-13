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
import com.wojet.pmtool.model.TaskPriority;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskPriorityDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.TaskPriorityRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.TaskPriorityService;

@Service
public class TaskPriorityServiceImpl extends GenericCrudService<TaskPriority, TaskPriorityDTO, TaskPriorityRepository>
    implements TaskPriorityService {

  @Autowired
  private TaskPriorityRepository taskPriorityRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all TaskPrioritys by project with pagination and sorting
   */
  public PagedResponse<TaskPriorityDTO> getTaskPrioritiesByProject(Long projectId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<TaskPriority> page = taskPriorityRepository.findByProjectId(projectId, pageable);

    List<TaskPriority> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<TaskPriorityDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<TaskPriorityDTO> response = new PagedResponse<>();
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
  public TaskPriorityDTO createTaskPriority(Long projectId, TaskPriorityDTO taskPriorityDTO) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", project.getClient().getId()));

    Consumer<TaskPriority> associations = TaskPriority -> {
      TaskPriority.setProject(project);
      TaskPriority.setClient(client);
    };

    return createWithAssociations(taskPriorityDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TaskPriorityDTO updateTaskPriority(Long taskPriorityId, TaskPriorityDTO taskPriorityDTO) {
    TaskPriority existingTaskPriority = taskPriorityRepository.findById(taskPriorityId)
        .orElseThrow(() -> new ResourceNotFoundException("TaskPriority", "id", taskPriorityId));

    Long projectId = taskPriorityDTO.getProjectId() == null ? existingTaskPriority.getProject().getId()
        : taskPriorityDTO.getProjectId();
    Project project = projectRepository.findById(
        projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<TaskPriority> associations = TaskPriority -> {
      TaskPriority.setProject(project);
      TaskPriority.setClient(client);
    };

    return updateWithAssociations(taskPriorityId, taskPriorityDTO, associations);
  }

  @Override
  public String deleteByProjectId(Long projectId) {
    taskPriorityRepository.deleteByProjectId(projectId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected TaskPriority mapToEntity(TaskPriorityDTO dto) {
    return modelMapper.map(dto, TaskPriority.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected TaskPriorityDTO mapToDTO(TaskPriority entity) {
    return modelMapper.map(entity, TaskPriorityDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(TaskPriority entity) {
    return taskPriorityRepository.findByProjectIdAndValue(entity.getProject().getId(), entity.getValue()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(TaskPriorityDTO dto, Long id) {
    TaskPriority existing = taskPriorityRepository.findByProjectIdAndValue(dto.getProjectId(), dto.getValue());
    return existing != null && !existing.getId().equals(id);
  }

}
