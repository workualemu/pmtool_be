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
import com.wojet.pmtool.payload.TaskPriorityDTO;
import com.wojet.pmtool.service.TaskPriorityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class TaskPriorityController {

  @Autowired
  private TaskPriorityService taskPriorityService;

  @GetMapping("/{projectId}/taskpriorities")
  public PagedResponse<TaskPriorityDTO> getByProject(
      @PathVariable Long projectId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return taskPriorityService.getTaskPrioritiesByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping("/{projectId}/taskpriority")
  public ResponseEntity<TaskPriorityDTO> create(
      @RequestBody TaskPriorityDTO taskPriorityDTO,
      @PathVariable Long projectId) {
    return new ResponseEntity<>(taskPriorityService.createTaskPriority(projectId, taskPriorityDTO), HttpStatus.CREATED);
  }

  @PutMapping("/taskpriorities/{taskPriorityId}")
  public ResponseEntity<TaskPriorityDTO> update(
      @Valid @PathVariable Long taskPriorityId,
      @RequestBody TaskPriorityDTO taskPriorityDTO) {
    TaskPriorityDTO updatedTag = taskPriorityService.updateTaskPriority(taskPriorityId, taskPriorityDTO);
    return new ResponseEntity<>(updatedTag, HttpStatus.OK);
  }

  @DeleteMapping("/taskpriorities/{taskPriorityId}")
  public ResponseEntity<TaskPriorityDTO> delete(
      @Valid @PathVariable Long taskPriorityId) {
    return new ResponseEntity<>(taskPriorityService.deleteById(taskPriorityId), HttpStatus.OK);
  }

  @DeleteMapping("/taskpriorities/{projectId}/all")
  public String deleteByProject(@PathVariable Long projectId) {
    return taskPriorityService.deleteByProjectId(projectId);
  }
}
