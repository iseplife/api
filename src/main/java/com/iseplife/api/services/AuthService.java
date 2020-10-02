package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Gallery;
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
   *
   * @return
   */
  static public boolean hasRoles(String... roles) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    for (String r : roles) {
      if (payload.getRoles().contains(r)) {
        return true;
      }
    }
    return false;
  }

  static public boolean isUserAnonymous() {
    // by default spring security set the principal as "anonymousUser"
    return SecurityContextHolder.getContext().getAuthentication()
      .getPrincipal().equals("anonymousUser");
  }

  static public Long getLoggedId() {
    if (!isUserAnonymous()) {
      return ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
    return null;
  }

  public Student getLoggedUser() {
    return studentService.getStudent(getLoggedId());
  }

  static public boolean hasRightOn(Post post) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || post.getAuthor().getId().equals(payload.getId())
            || payload.getClubsPublisher().contains(post.getLinkedClub().getId())
            || hasRightOn(post.getFeed());
  }

  static public boolean hasRightOn(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || (feed.getClub() != null && payload.getClubsPublisher().contains(feed.getClub().getId()))
            || (feed.getEvent() != null && payload.getClubsPublisher().contains(feed.getEvent().getClub().getId()));
  }

  static public boolean hasReadAccess(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || (feed.getClub() != null)
            || (feed.getGroup() != null && payload.getFeeds().contains(feed.getId()))
            || (feed.getEvent() != null && feed.getEvent().getTargets().stream().anyMatch(f -> payload.getFeeds().contains(f.getId())));
  }

  static public boolean hasRightOn(Group group) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || group.getMembers().stream().anyMatch(m -> m.getStudent().getId().equals(payload.getId()) && m.isAdmin());
  }

  static public boolean hasRightOn(Gallery gallery) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || payload.getClubsPublisher().contains(gallery.getClub().getId());
  }

  static public boolean hasRightOn(Club club) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || payload.getClubsAdmin().contains(club.getId());
  }

  static public boolean hasRightOn(Event event) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || payload.getClubsPublisher().contains(event.getClub().getId())
            || payload.getClubsAdmin().contains(event.getClub().getId());
  }

  public static boolean userHasRole(String role) {
    return ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains(role);
  }
}
