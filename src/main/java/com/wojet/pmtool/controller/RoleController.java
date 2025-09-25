package com.wojet.pmtool.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.wojet.pmtool.payload.RoleDTO;
import com.wojet.pmtool.service.RoleService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/{clientId}/roles")
    public PagedResponse<RoleDTO> getRolesByClient(
            @PathVariable Long clientId,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return roleService.getRolesByClient(clientId, pageNumber, pageSize, sortBy, sortDir);
    }

    @PostMapping("/{clientId}/role")
    public ResponseEntity<RoleDTO> addRole(
            @RequestBody RoleDTO roleDto,
            @PathVariable Long clientId) {
        return new ResponseEntity<>(roleService.createRole(clientId, roleDto), HttpStatus.CREATED);
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<RoleDTO> updateRole(
            @Valid @PathVariable Long roleId,
            @RequestBody RoleDTO roleDTO) {
        RoleDTO updatedRole = roleService.updateRole(roleId, roleDTO);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<RoleDTO> deleteRole(
            @Valid @PathVariable Long roleId) {
        return new ResponseEntity<>(roleService.deleteById(roleId), HttpStatus.OK);
    }

}
