package com.wojet.pmtool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.service.ProjectService;


@RestController
@RequestMapping("/api/client/admin")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // @GetMapping("/projects")
    // public ResponseEntity<List<Project>> getAllProjects() {
    //     return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    // }

    @PostMapping("/{clientId}/project")
    public ResponseEntity<ProjectDTO> addProject(
        @RequestBody Project project,
        @PathVariable Long clientId
    ) {
        return new ResponseEntity<>(projectService.addProject(clientId, project), HttpStatus.CREATED);
    }
}
