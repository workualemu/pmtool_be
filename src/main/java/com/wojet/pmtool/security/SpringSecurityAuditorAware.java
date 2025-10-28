package com.wojet.pmtool.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wojet.pmtool.security.service.UserDetailsImpl;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Autowired
    // private UserRepository userRepository;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl u) {
            return Optional.ofNullable(u.getId());
        }

        return Optional.empty();

    }
}
