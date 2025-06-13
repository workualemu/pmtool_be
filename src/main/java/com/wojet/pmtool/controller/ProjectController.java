package com.wojet.pmtool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public PagedResponse<ProjectDTO> getAllProjects(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return projectService.getAll(pageNumber, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{clientId}/projects")
    public PagedResponse<ProjectDTO> getProjectsByClient(
            @PathVariable Long clientId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return projectService.getProjectsByClient(clientId, pageNumber, pageSize, sortBy, sortDir);
    }

    @PostMapping("/{clientId}/project")
    public ResponseEntity<ProjectDTO> addProject(
            @RequestBody ProjectDTO projectDto,
            @PathVariable Long clientId) {
        return new ResponseEntity<>(projectService.createProject(clientId, projectDto), HttpStatus.CREATED);
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(
            @Valid @PathVariable Long projectId,
            @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedClient = projectService.updateProject(projectId, projectDTO);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDTO> deleteProject(
            @Valid @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.deleteById(projectId), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{clientId}/all")
    public String deleteAllProjectsByClient(@PathVariable Long clientId) {
        return projectService.deleteByClientId(clientId);
    }
}
