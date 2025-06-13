package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TagDTO;

public interface TagService {

  PagedResponse<TagDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<TagDTO> getTagsByProject(Long projectId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir);

  TagDTO createTag(Long projectId, TagDTO tagDTO);

  TagDTO updateTag(Long id, TagDTO tagDTO);

  TagDTO deleteById(Long id);

  String deleteByProjectId(Long projectId);
}
