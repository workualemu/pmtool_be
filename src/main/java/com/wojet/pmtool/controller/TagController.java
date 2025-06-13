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
import com.wojet.pmtool.payload.TagDTO;
import com.wojet.pmtool.service.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/client/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    // @GetMapping("/tags")
    // public PagedResponse<TagDTO> getAllProjects(
    // @RequestParam(name = "pageNumber", defaultValue = "0", required = false)
    // Integer pageNumber,
    // @RequestParam(name = "pageSize", defaultValue = "10", required = false)
    // Integer pageSize,
    // @RequestParam(name = "sortBy", defaultValue = "id", required = false) String
    // sortBy,
    // @RequestParam(name = "sortDir", defaultValue = "asc", required = false)
    // String sortDir) {
    // return tagService.getAll(pageNumber, pageSize, sortBy, sortDir);
    // }

    @GetMapping("/{projectId}/tags")
    public PagedResponse<TagDTO> getTagsByProject(
            @PathVariable Long projectId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return tagService.getTagsByProject(projectId, pageNumber, pageSize, sortBy, sortDir);
    }

    @PostMapping("/{projectId}/tag")
    public ResponseEntity<TagDTO> addTag(
            @RequestBody TagDTO tagDto,
            @PathVariable Long projectId) {
        return new ResponseEntity<>(tagService.createTag(projectId, tagDto), HttpStatus.CREATED);
    }

    @PutMapping("/tags/{tagId}")
    public ResponseEntity<TagDTO> updateTag(
            @Valid @PathVariable Long tagId,
            @RequestBody TagDTO tagDTO) {
        TagDTO updatedTag = tagService.updateTag(tagId, tagDTO);
        return new ResponseEntity<>(updatedTag, HttpStatus.OK);
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<TagDTO> deleteTag(
            @Valid @PathVariable Long tagId) {
        return new ResponseEntity<>(tagService.deleteById(tagId), HttpStatus.OK);
    }

    @DeleteMapping("/tags/{projectId}/all")
    public String deleteAllTagsByProject(@PathVariable Long projectId) {
        return tagService.deleteByProjectId(projectId);
    }
}
