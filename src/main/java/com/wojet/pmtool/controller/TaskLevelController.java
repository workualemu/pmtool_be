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
import com.wojet.pmtool.payload.TaskLevelDTO;
import com.wojet.pmtool.service.TaskLevelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class TaskLevelController {

  @Autowired
  private TaskLevelService taskLevelService;

  @GetMapping("/{projectId}/task-levels")
  public PagedResponse<TaskLevelDTO> getByProject(
      @PathVariable Long projectId,
      @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
    return taskLevelService.getByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
  }

  @PostMapping("/{projectId}/task-level")
  public ResponseEntity<TaskLevelDTO> create(
      @RequestBody TaskLevelDTO taskLevelDTO,
      @PathVariable Long projectId) {
    return new ResponseEntity<>(taskLevelService.createTaskLevel(projectId, taskLevelDTO), HttpStatus.CREATED);
  }

  @PutMapping("/task-levels/{taskLevelId}")
  public ResponseEntity<TaskLevelDTO> update(
      @Valid @PathVariable Long taskLevelId,
      @RequestBody TaskLevelDTO taskLevelDTO) {
    TaskLevelDTO updatedTag = taskLevelService.updateTaskLevel(taskLevelId, taskLevelDTO);
    return new ResponseEntity<>(updatedTag, HttpStatus.OK);
  }

  @DeleteMapping("/task-levels/{taskLevelId}")
  public ResponseEntity<TaskLevelDTO> delete(
      @Valid @PathVariable Long taskLevelId) {
    return new ResponseEntity<>(taskLevelService.deleteById(taskLevelId), HttpStatus.OK);
  }

  @DeleteMapping("/task-levels/{projectId}/all")
  public String deleteByProject(@PathVariable Long projectId) {
    return taskLevelService.deleteByProjectId(projectId);
  }
}
