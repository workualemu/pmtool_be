package com.wojet.pmtool.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.AppRole;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Role;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.RoleRepository;
import com.wojet.pmtool.repository.UserRepository;
import com.wojet.pmtool.security.jwt.JwtUtils;
import com.wojet.pmtool.security.jwt.LoginRequest;
import com.wojet.pmtool.security.jwt.LoginResponse;
import com.wojet.pmtool.security.jwt.MessageResponse;
import com.wojet.pmtool.security.jwt.SignupRequest;
import com.wojet.pmtool.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClientRepository clientRepository;


    // @PostMapping("/register")
    // public Map<String, Object> register(@RequestBody Map<String, String> body) {
    //     String email = body.get("email");
    //     String password = body.get("password");

    //     User user = userService.registerUser(email, password);

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("message", "User registered successfully!");
    //     response.put("userId", user.getId());
    //     return response;
    // }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }
        
        Optional<Client> optionalClient = clientRepository.getClientById(signupRequest.getClient_id());
        if(!optionalClient.isPresent())
            throw new APIException("Client with Client_ID '" + signupRequest.getClient_id() + "'not found !!!");
        Client client = optionalClient.get();

        User user = new User(signupRequest.getEmail(), 
            passwordEncoder.encode(signupRequest.getPassword()), client);
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else{
            strRoles.forEach(role -> {
                switch(role) {
                    case "sys_admin" -> {
                        Role mod_role = roleRepository.findByRoleName(AppRole.ROLE_SYS_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(mod_role);
                    }
                    case "client_admin" -> {
                        Role mod_role = roleRepository.findByRoleName(AppRole.ROLE_CLIENT_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(mod_role);
                    }
                    case "project_manager" -> {
                        Role mod_role = roleRepository.findByRoleName(AppRole.ROLE_PROJECT_MANAGER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(mod_role);
                    }
                    default -> {
                        Role mod_role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(mod_role);
                    }
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Bad credentials");
            error.put("status", false);
            return new ResponseEntity<Object>(error, HttpStatus.UNAUTHORIZED);
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .toList();
        LoginResponse loginResponse = new LoginResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), 
            roles);
        return ResponseEntity.ok(loginResponse);

    }
    
    // @PostMapping("/login")
    // public Map<String, Object> login(@RequestBody Map<String, String> body) {
    //     String email = body.get("email");
    //     String password = body.get("password");

    //     return userService.loginUser(email, password)
    //         .map(user -> {
    //             Map<String, Object> response = new HashMap<>();
    //             response.put("message", "Login successful!");
    //             response.put("token", "dummy-token-" + user.getId()); // Temporary token
    //             return response;
    //         })
    //         .orElseGet(() -> {
    //             Map<String, Object> error = new HashMap<>();
    //             error.put("error", "Invalid credentials");
    //             return error;
    //         });
    // }
}
