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
import com.wojet.pmtool.payload.TaskDTO;
import com.wojet.pmtool.service.TaskService;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
public class ProjectTasksController {

  @Autowired
  private TaskService taskService;

  @GetMapping()
  public PagedResponse<TaskDTO> getTasksByProject(
      @PathVariable Long projectId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return taskService.getTasksByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping()
  public ResponseEntity<TaskDTO> addTaskToProject(
      @RequestBody TaskDTO taskDto,
      @PathVariable Long projectId) {
    return new ResponseEntity<>(taskService.createTask(projectId, taskDto), HttpStatus.CREATED);
  }

  @DeleteMapping()
  public String deleteAllTasksByProject(@PathVariable Long projectId) {
    return taskService.deleteByProjectId(projectId);
  }
}
