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
import com.wojet.pmtool.model.Task;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.TaskRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.TaskService;

@Service
public class TaskServiceImpl extends GenericCrudService<Task, TaskDTO, TaskRepository>
    implements TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all tasks with pagination and sorting
   */
  public PagedResponse<TaskDTO> getTasksByProject(Long projectId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<Task> page = taskRepository.findByProjectId(projectId, pageable);

    List<Task> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<TaskDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<TaskDTO> response = new PagedResponse<>();
    response.setContent(dtos);
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLastPage(page.isLast());

    return response;
  }

  /**
   * Get all tasks with pagination and sorting
   */
  @Override
  public TaskDTO createTask(Long projectId, TaskDTO taskDTO) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", project.getClient().getId()));

    Consumer<Task> associations = Task -> {
      Task.setProject(project);
      Task.setClient(client);
    };
    

    return createWithAssociations(taskDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TaskDTO updateTask(Long TaskId, TaskDTO taskDTO) {
    Task existingTask = taskRepository.findById(TaskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task", "id", TaskId));

    Long projectId = taskDTO.getProjectId() == null ? existingTask.getProject().getId() : taskDTO.getProjectId();
    Project project = projectRepository.findById(
        projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<Task> associations = Task -> {
      Task.setProject(project);
      Task.setClient(client);
    };

    return updateWithAssociations(TaskId, taskDTO, associations);
  }

  @Override
  public String deleteByProjectId(Long projectId) {
    taskRepository.deleteByProjectId(projectId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected Task mapToEntity(TaskDTO dto) {
    return modelMapper.map(dto, Task.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected TaskDTO mapToDTO(Task entity) {
    return modelMapper.map(entity, TaskDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(Task entity) {
    return taskRepository.findByProjectIdAndTitle(entity.getProject().getId(), entity.getTitle()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(TaskDTO dto, Long id) {
    Task existing = taskRepository.findByProjectIdAndTitle(dto.getProjectId(), dto.getTitle());
    return existing != null && !existing.getId().equals(id);
  }

}
