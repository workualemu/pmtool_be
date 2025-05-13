package com.wojet.pmtool.service;

import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.payload.ProjectDTO;

public interface ProjectService {

    // ProjectResponse getAllProjects(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    // ProjectDTO getProjectById(Long projectId);
    // public ProductRe
    public ProjectDTO addProject(Long clientId, Project project);
    // ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    // ProjectDTO deleteProject(Long id);
    // String deleteAllProjects();
}
