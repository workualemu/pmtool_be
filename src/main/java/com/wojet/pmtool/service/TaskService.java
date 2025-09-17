package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskDTO;

public interface TaskService {

  /**
   * Retrieves a task by its project ID and subtitle.
   *
   * @param projectId the ID of the project
   * @param subTitle  the subtitle of the task
   * @return the TaskDTO if found, null otherwise
   */
  PagedResponse<TaskDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  /**
   * Retrieves all tasks associated with a specific project.
   *
   * @param projectId  the ID of the project
   * @param pageNumber the page number for pagination
   * @param pageSize   the size of each page
   * @param sortBy     the field to sort by
   * @param sortDir    the direction of sorting (asc/desc)
   * @return a PagedResponse containing TaskDTOs
   */
  PagedResponse<TaskDTO> getTasksByProject(Long projectId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir);

  /**
   * Creates a new task associated with a specific project.
   *
   * @param projectId the ID of the project
   * @param taskDTO   the TaskDTO containing task details
   * @return the created TaskDTO
   */
  TaskDTO createTask(Long projectId, TaskDTO taskDTO);

  /**
   * Updates an existing task by its ID.
   *
   * @param id      the ID of the task to update
   * @param taskDTO the TaskDTO containing updated task details
   * @return the updated TaskDTO
   */
  TaskDTO updateTask(Long id, TaskDTO taskDTO);

  /**
   * Deletes a task by its ID.
   *
   * @param id the ID of the task to delete
   * @return the deleted TaskDTO
   */
  TaskDTO deleteById(Long id);

  /**
   * Deletes all tasks associated with a specific project.
   *
   * @param projectId the ID of the project
   * @return a message indicating the result of the deletion
   */
  String deleteByProjectId(Long projectId);
}
