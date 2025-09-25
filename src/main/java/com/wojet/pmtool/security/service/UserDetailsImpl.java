package com.wojet.pmtool.security.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Long clientId;
    private String clientName;
    private Long recentProjectId;

    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, String firstName, String lastName, 
            Collection<? extends GrantedAuthority> authorities,
            Long clientId, String clientName, Long recentProjectId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.clientId = clientId;
        this.clientName = clientName;
        this.recentProjectId = recentProjectId;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        Client client = user.getClient();
        Project recentProject = user.getRecentProject();
        return new UserDetailsImpl(
                user.getId(), 
                user.getEmail(), 
                user.getPassword(),
                user.getFirstName(), 
                user.getLastName(), 
                authorities,
                client==null ? null : client.getId(),
                client==null ? null : client.getName(),
                recentProject==null ? null : recentProject.getId()
            );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
    
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl [id=" + id + ", first name=" + firstName + ", last name=" + lastName + ", email=" + email + ", password=" + password
                + ", authorities=" + authorities + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserDetailsImpl))
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return id != null && id.equals(user.getId());
    }

}
