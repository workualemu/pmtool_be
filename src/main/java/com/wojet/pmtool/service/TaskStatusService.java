package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskStatusDTO;

public interface TaskStatusService {

  PagedResponse<TaskStatusDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<TaskStatusDTO> getByProject(Long projectId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir);

  TaskStatusDTO createTaskStatus(Long projectId, TaskStatusDTO taskStatusDTO);

  TaskStatusDTO updateTaskStatus(Long id, TaskStatusDTO taskStatusDTO);

  TaskStatusDTO deleteById(Long id);

  String deleteByProjectId(Long projectId);
}
