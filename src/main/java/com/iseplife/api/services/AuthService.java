package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.entity.user.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by Guillaume on 13/08/2017.
 * back
 */
@Service
public class AuthService {

  @Autowired
  StudentService studentService;

  /**
   * Check if user has one of the roles listed
   * @return
   */
  public boolean hasRoles(TokenPayload payload, String... roles) {
    for (String r: roles) {
      if (payload.getRoles().contains(r)) {
        return true;
      }
    }
    return false;
  }

  public boolean isUserAnonymous() {
    // by default spring security set the principal as "anonymousUser"
    return SecurityContextHolder.getContext().getAuthentication()
      .getPrincipal().equals("anonymousUser");
  }

  public Long getLoggedId() {
    if (!isUserAnonymous()) {
      return ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
    return null;
  }

  public Student getLoggedUser() {
    if (!isUserAnonymous()) {
      Long id = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
      return studentService.getStudent(id);
    }
    return null;
  }

  public boolean userHasRole(TokenPayload auth, String role) {
    return auth.getRoles().contains(role);
  }
}
