package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TaskLevelDTO;

public interface TaskLevelService {

  PagedResponse<TaskLevelDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<TaskLevelDTO> getByProject(Long projectId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir);

  TaskLevelDTO createTaskLevel(Long projectId, TaskLevelDTO TaskLevelDTO);

  TaskLevelDTO updateTaskLevel(Long id, TaskLevelDTO TaskLevelDTO);

  TaskLevelDTO deleteById(Long id);

  String deleteByProjectId(Long projectId);
}
