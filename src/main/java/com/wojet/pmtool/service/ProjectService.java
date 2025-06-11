package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.ProjectDTO;

public interface ProjectService {

    PagedResponse<ProjectDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    PagedResponse<ProjectDTO> getProjectsByClient(Long clientId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    // ProjectDTO getProjectById(Long projectId);
    ProjectDTO createProject(Long clientId, ProjectDTO projectDTO);
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    ProjectDTO deleteById(Long id);
    String deleteByClientId(Long clientId);
}
