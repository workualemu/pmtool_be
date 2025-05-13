package com.wojet.pmtool.security.jwt;

import java.util.Set;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    
    @Email
    private String email;

    private String password;
    private Set<String> roles;
    private Long client_id;
}
