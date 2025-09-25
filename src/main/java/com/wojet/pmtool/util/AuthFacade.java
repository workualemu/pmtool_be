package com.wojet.pmtool.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wojet.pmtool.security.service.UserDetailsImpl;

@Component
public class AuthFacade {
  public Authentication getAuth() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public UserDetailsImpl currentUser() {
    var a = getAuth();
    if (a == null || !a.isAuthenticated() || a.getPrincipal() instanceof String)
      return null;
    return (UserDetailsImpl) a.getPrincipal();
  }

}