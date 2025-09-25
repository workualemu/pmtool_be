package com.wojet.pmtool.service;

import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.RoleDTO;

public interface RoleService {

  // PagedResponse<RoleDTO> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

  PagedResponse<RoleDTO> getRolesByClient(Long clientId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir);

  RoleDTO createRole(Long clientId, RoleDTO roleDTO);

  RoleDTO updateRole(Long id, RoleDTO roleDTO);

  RoleDTO deleteById(Long id);

}
