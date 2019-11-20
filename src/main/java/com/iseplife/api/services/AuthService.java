package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.Post;
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
  public boolean hasRoles(String... roles) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
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
    return studentService.getStudent(getLoggedId());
  }

  public boolean hasRightOn(Post post) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return payload.getRoles().contains(Roles.ADMIN)
      || post.getAuthor().getId().equals(payload.getId())
      || payload.getClubsPublisher().contains(post.getLinkedClub().getId())
      || hasRightOn(post.getFeed());
  }

  public boolean hasRightOn(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return payload.getRoles().contains(Roles.ADMIN)
      || (feed.getClub() != null && payload.getClubsPublisher().contains(feed.getClub().getId()))
      || (feed.getEvent() != null && payload.getClubsPublisher().contains(feed.getEvent().getClub().getId()) );
  }


  public boolean userHasRole(String role) {
    return ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains(role);
  }
}
