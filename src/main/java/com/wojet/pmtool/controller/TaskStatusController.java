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
import com.wojet.pmtool.payload.TaskStatusDTO;
import com.wojet.pmtool.service.TaskStatusService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class TaskStatusController {

  @Autowired
  private TaskStatusService taskStatusService;

  @GetMapping("/{projectId}/task-statuses")
  public PagedResponse<TaskStatusDTO> getByProject(
      @PathVariable Long projectId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return taskStatusService.getByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping("/{projectId}/task-status")
  public ResponseEntity<TaskStatusDTO> create(
      @RequestBody TaskStatusDTO taskStatusDTO,
      @PathVariable Long projectId) {
    return new ResponseEntity<>(taskStatusService.createTaskStatus(projectId, taskStatusDTO), HttpStatus.CREATED);
  }

  @PutMapping("/task-statuses/{taskStatusId}")
  public ResponseEntity<TaskStatusDTO> update(
      @Valid @PathVariable Long taskStatusId,
      @RequestBody TaskStatusDTO taskStatusDTO) {
    TaskStatusDTO updatedTag = taskStatusService.updateTaskStatus(taskStatusId, taskStatusDTO);
    return new ResponseEntity<>(updatedTag, HttpStatus.OK);
  }

  @DeleteMapping("/task-statuses/{taskStatusId}")
  public ResponseEntity<TaskStatusDTO> delete(
      @Valid @PathVariable Long taskStatusId) {
    return new ResponseEntity<>(taskStatusService.deleteById(taskStatusId), HttpStatus.OK);
  }

  @DeleteMapping("/task-statuses/{projectId}/all")
  public String deleteByProject(@PathVariable Long projectId) {
    return taskStatusService.deleteByProjectId(projectId);
  }
}
