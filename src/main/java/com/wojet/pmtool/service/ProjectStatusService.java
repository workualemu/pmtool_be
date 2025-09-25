package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.ProjectStatusDTO;

public interface ProjectStatusService {

  PagedResponse<ProjectStatusDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<ProjectStatusDTO> getByClient(Long clientId, Integer pageNumber, Integer pageSize,
      String sortBy,
      String sortDir);

  ProjectStatusDTO createProjectStatus(Long clientId, ProjectStatusDTO projectStatusDTO);

  ProjectStatusDTO updateProjectStatus(Long id, ProjectStatusDTO projectStatusDTO);

  ProjectStatusDTO deleteById(Long id);

  String deleteByClientId(Long clientId);
}
