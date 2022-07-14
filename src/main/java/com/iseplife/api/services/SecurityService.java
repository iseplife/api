package com.iseplife.api.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.conf.jwt.TokenSet;
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
  final private JwtTokenUtil jwtTokenUtil;
  final private HttpServletResponse response;

  @Value("${jwt.refresh-token-duration}")
  private int refreshTokenDuration;

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
    if (userHasRole(Roles.ADMIN))
      return true;

    switch (feed.getType()){
      case EVENT:
        List<Long> targetIds = feed.getEvent().getTargets().stream().map(Feed::getId).collect(Collectors.toList());
        return targetIds.size() == 0 || payload.getFeeds().stream().filter(authorizedFeed -> targetIds.contains(authorizedFeed)).count() > 0;
      case STUDENT:
        return payload.getId().equals(feed.getStudent().getId());
      case CLUB:
        return payload.getClubsPublisher().contains(feed.getClub().getId());
      case GROUP:
        return payload.getFeeds().contains(feed.getId());
      default:
        return false;
    }
  }

  static public boolean hasReadAccess(Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return hasReadAccess(feed, payload);
  }
  static public boolean hasReadAccess(PostProjection post, Feed feed) {
    TokenPayload payload = ((TokenPayload) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return hasReadAccess(feed, payload) && (!post.getPublicationDate().after(new Date()) || hasRightOn(post));
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
            || gallery.getClub() == null
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

  public TokenSet logUser(Student student) {
    TokenSet set = jwtTokenUtil.generateTokenSet(student);
    return setRefreshTokenCookie(set);
  }

  public TokenSet refreshUserToken(String refreshToken) {
    TokenSet set = jwtTokenUtil.refreshWithToken(refreshToken);

    return setRefreshTokenCookie(set);
  }

  private TokenSet setRefreshTokenCookie(TokenSet set) {
    Cookie cRefreshToken = new Cookie("refresh-token", set.getRefreshToken());
    cRefreshToken.setMaxAge(refreshTokenDuration);
    cRefreshToken.setPath("/");
    cRefreshToken.setHttpOnly(true);
    cRefreshToken.setSecure(true);

    response.addCookie(cRefreshToken);

    return set;
  }

}
