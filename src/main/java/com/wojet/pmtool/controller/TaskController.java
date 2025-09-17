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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @GetMapping("/{projectId}/tasks")
  public PagedResponse<TaskDTO> getTasksByProject(
      @PathVariable Long projectId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return taskService.getTasksByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping("/{projectId}/task")
  public ResponseEntity<TaskDTO> addTask(
      @RequestBody TaskDTO taskDto,
      @PathVariable Long projectId) {
    return new ResponseEntity<>(taskService.createTask(projectId, taskDto), HttpStatus.CREATED);
  }

  @PutMapping("/tasks/{taskId}")
  public ResponseEntity<TaskDTO> updateTask(
      @PathVariable("taskId") Long taskId,
      @RequestBody TaskDTO taskDTO) {
    TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO);
    return new ResponseEntity<>(updatedTask, HttpStatus.OK);
  }

  @DeleteMapping("/tasks/{taskId}")
  public ResponseEntity<TaskDTO> deleteTask(
      @PathVariable("taskId") Long taskId) {
    return new ResponseEntity<>(taskService.deleteById(taskId), HttpStatus.OK);
  }

  @DeleteMapping("/tasks/{projectId}/all")
  public String deleteAllTasksByProject(@PathVariable Long projectId) {
    return taskService.deleteByProjectId(projectId);
  }
}
