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
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.TaskLevel;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskLevelDTO;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.TaskLevelRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.TaskLevelService;

@Service
public class TaskLevelServiceImpl extends GenericCrudService<TaskLevel, TaskLevelDTO, TaskLevelRepository>
    implements TaskLevelService {

  @Autowired
  private TaskLevelRepository taskLevelRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all TaskLevels by project with pagination and sorting
   */
  public PagedResponse<TaskLevelDTO> getByProject(Long projectId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<TaskLevel> page = taskLevelRepository.findByProjectId(projectId, pageable);

    List<TaskLevel> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<TaskLevelDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<TaskLevelDTO> response = new PagedResponse<>();
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
  public TaskLevelDTO createTaskLevel(Long projectId, TaskLevelDTO TaskLevelDTO) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<TaskLevel> associations = TaskLevel -> {
      TaskLevel.setProject(project);
    };

    return createWithAssociations(TaskLevelDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TaskLevelDTO updateTaskLevel(Long taskLevelId, TaskLevelDTO taskLevelDTO) {
    TaskLevel existingTaskLevel = taskLevelRepository.findById(taskLevelId)
        .orElseThrow(() -> new ResourceNotFoundException("TaskLevel", "id", taskLevelId));

    Long projectId = taskLevelDTO.getProjectId() == null ? existingTaskLevel.getProject().getId()
        : taskLevelDTO.getProjectId();
    Project project = projectRepository.findById(
        projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<TaskLevel> associations = TaskLevel -> {
      TaskLevel.setProject(project);
    };

    return updateWithAssociations(taskLevelId, taskLevelDTO, associations);
  }

  @Override
  public String deleteByProjectId(Long projectId) {
    taskLevelRepository.deleteByProjectId(projectId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected TaskLevel mapToEntity(TaskLevelDTO dto) {
    return modelMapper.map(dto, TaskLevel.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected TaskLevelDTO mapToDTO(TaskLevel entity) {
    return modelMapper.map(entity, TaskLevelDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(TaskLevel entity) {
    return taskLevelRepository.findByProjectIdAndLevel(entity.getProject().getId(), entity.getLevel()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(TaskLevelDTO dto, Long id) {
    TaskLevel existing = taskLevelRepository.findByProjectIdAndLevel(dto.getProjectId(), dto.getLevel());
    return existing != null && !existing.getId().equals(id);
  }

}
