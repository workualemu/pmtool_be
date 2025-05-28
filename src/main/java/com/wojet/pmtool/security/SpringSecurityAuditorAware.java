package com.wojet.pmtool.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wojet.pmtool.model.User;
import com.wojet.pmtool.repository.UserRepository;
import com.wojet.pmtool.security.service.UserDetailsImpl;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            Long userId = ((UserDetailsImpl) principal).getId();
            return userRepository.findById(userId);
        }

        return Optional.empty();

    }
}
