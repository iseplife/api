package com.iseplife.api.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SecurityService {
  @Lazy final private StudentService studentService;

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
    return hasRightOn(post, payload);
  }

  static public boolean hasRightOn(Post post, TokenPayload payload) {
    return userHasRole(Roles.ADMIN)
      || post.getAuthor().getId().equals(payload.getId())
      || post.getLinkedClub() != null && payload.getClubsPublisher().contains(post.getLinkedClub().getId());
  }

  static public boolean hasRightOn(PostProjection post) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || post.getAuthor().getId().equals(payload.getId())
            || payload.getClubsPublisher().contains(post.getAuthor().getId());
  }

  static public boolean hasRightOn(CommentProjection comment) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
      || comment.getAuthor().getId().equals(payload.getId())
      || payload.getClubsPublisher().contains(comment.getAuthor().getId());
  }

  static public boolean hasRightOn(Comment comment) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
      || comment.getStudent().getId().equals(payload.getId())
      || payload.getClubsPublisher().contains(comment.getAsClub().getId())
      || hasRightOn(comment.getParentThread().getFeed());
  }

  static public boolean hasRightOn(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || (feed.getGroup() != null && payload.getFeeds().contains(feed.getId()))
            || (feed.getClub() != null && payload.getClubsPublisher().contains(feed.getClub().getId()))
            || (feed.getEvent() != null && payload.getClubsPublisher().contains(feed.getEvent().getClub().getId()))
            || (feed.getStudent() != null && payload.getId().equals(feed.getStudent().getId()));
  }

  static public boolean hasReadAccess(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return hasReadAccess(feed, payload);
  }
  static public boolean hasReadAccess(Feed feed, TokenPayload payload) {
    return userHasRole(Roles.ADMIN)
            || (feed.getStudent() != null)
            || (feed.getClub() != null)
            || (feed.getGroup() != null && payload.getFeeds().contains(feed.getId()))
            || (feed.getEvent() != null && (feed.getEvent().getTargets().size() == 0 || feed.getEvent().getTargets().stream().anyMatch(f -> payload.getFeeds().contains(f.getId()))));
  }
  static public boolean hasReadAccess(Group group) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    System.out.println(payload.getFeeds());
    System.out.println(group.getFeed().getId());
    System.out.println(payload.getFeeds().contains(group.getFeed().getId()));
    return payload.getFeeds().contains(group.getFeed().getId());
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

  static public boolean hasAuthorAccessOn(Long clubId) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
      || payload.getClubsAdmin().contains(clubId)
      || payload.getClubsPublisher().contains(clubId);
  }

  static public boolean hasRightOn(Event event) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
            || payload.getClubsPublisher().contains(event.getClub().getId())
            || payload.getClubsAdmin().contains(event.getClub().getId());
  }

  static public boolean hasReadAccessOn(Event event) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return userHasRole(Roles.ADMIN)
      || event.getTargets().size() == 0
      || event.getTargets().stream().anyMatch(f -> payload.getFeeds().contains(f.getId()));
  }

  public static boolean userHasRole(String role) {
    return ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().contains(role);
  }
}
