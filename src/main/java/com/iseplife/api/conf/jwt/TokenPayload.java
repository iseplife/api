package com.iseplife.api.conf.jwt;

import java.util.List;

/**
 * Created by Guillaume on 17/10/2017.
 * back
 */
public class TokenPayload {
  private Long id;

  private List<String> roles;
  private List<Long> clubsAdmin;
  private List<Long> clubsPublisher;
  private List<Long> feeds;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<Long> getClubsAdmin() {
    return clubsAdmin;
  }

  void setClubsAdmin(List<Long> clubsAdmin) {
    this.clubsAdmin = clubsAdmin;
  }

  public List<Long> getClubsPublisher() {
    return clubsPublisher;
  }

  void setClubsPublisher(List<Long> clubsPublisher) {
    this.clubsPublisher = clubsPublisher;
  }

  @Override
  public String toString() {
    return "TokenPayload{" +
      "id=" + id +
      ", roles=" + roles +
      ", clubsAdmin=" + clubsAdmin +
      '}';
  }

  public List<Long> getFeeds() {
    return feeds;
  }

  public void setFeeds(List<Long> feeds) {
    this.feeds = feeds;
  }
}
