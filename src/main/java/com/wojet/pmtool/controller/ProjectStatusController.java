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
import com.wojet.pmtool.payload.ProjectStatusDTO;
import com.wojet.pmtool.service.ProjectStatusService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class ProjectStatusController {

  @Autowired
  private ProjectStatusService projectStatusService;

  @GetMapping("/{clientId}/project-statuses")
  public PagedResponse<ProjectStatusDTO> getByClient(
      @PathVariable Long clientId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return projectStatusService.getByClient(clientId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping("/{clientId}/project-status")
  public ResponseEntity<ProjectStatusDTO> create(
      @RequestBody ProjectStatusDTO projectStatusDTO,
      @PathVariable Long clientId) {
    return new ResponseEntity<>(projectStatusService.createProjectStatus(clientId, projectStatusDTO),
        HttpStatus.CREATED);
  }

  @PutMapping("/project-statuses/{projectStatusId}")
  public ResponseEntity<ProjectStatusDTO> update(
      @Valid @PathVariable Long projectStatusId,
      @RequestBody ProjectStatusDTO projectStatusDTO) {
    ProjectStatusDTO updatedTag = projectStatusService.updateProjectStatus(projectStatusId, projectStatusDTO);
    return new ResponseEntity<>(updatedTag, HttpStatus.OK);
  }

  @DeleteMapping("/project-statuses/{projectStatusId}")
  public ResponseEntity<ProjectStatusDTO> delete(
      @Valid @PathVariable Long projectStatusId) {
    return new ResponseEntity<>(projectStatusService.deleteById(projectStatusId), HttpStatus.OK);
  }

  @DeleteMapping("/project-statuses/{clientId}/all")
  public String deleteByClient(@PathVariable Long clientId) {
    return projectStatusService.deleteByClientId(clientId);
  }
}
