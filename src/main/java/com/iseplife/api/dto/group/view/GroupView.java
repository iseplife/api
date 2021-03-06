package com.iseplife.api.dto.group.view;


public class GroupView {
  private Long id;
  private String name;
  private Boolean restricted;
  private Boolean archived;
  private String cover;
  private Long feed;
  private Boolean hasRight;
  private Boolean subscribed;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getRestricted() {
    return restricted;
  }

  public void setRestricted(Boolean restricted) {
    this.restricted = restricted;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }

  public Boolean getHasRight() {
    return hasRight;
  }

  public void setHasRight(Boolean hasRight) {
    this.hasRight = hasRight;
  }

  public Boolean getSubscribed() {
    return subscribed;
  }

  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }
}
