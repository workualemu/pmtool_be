package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskPriorityDTO;

public interface TaskPriorityService {

  PagedResponse<TaskPriorityDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<TaskPriorityDTO> getTaskPrioritiesByProject(Long projectId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir);

  TaskPriorityDTO createTaskPriority(Long projectId, TaskPriorityDTO taskPriorityDTO);

  TaskPriorityDTO updateTaskPriority(Long id, TaskPriorityDTO taskPriorityDTO);

  TaskPriorityDTO deleteById(Long id);

  String deleteByProjectId(Long projectId);
}
