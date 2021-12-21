package com.iseplife.api.dto.survey;

import java.util.Date;
import java.util.Set;

public class SurveyDTO {
  private Long id;
  private Boolean enabled;
  private String title;
  private Date opensAt;
  private Date closesAt;
  private Boolean anonymous;
  private Boolean multiple;
  private Set<Long> feeds;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getOpensAt() {
    return opensAt;
  }

  public void setOpensAt(Date opensAt) {
    this.opensAt = opensAt;
  }

  public Date getClosesAt() {
    return closesAt;
  }

  public void setClosesAt(Date closesAt) {
    this.closesAt = closesAt;
  }

  public Boolean getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(Boolean anonymous) {
    this.anonymous = anonymous;
  }

  public Boolean getMultiple() {
    return multiple;
  }

  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  public Set<Long> getFeeds() {
    return feeds;
  }

  public void setFeeds(Set<Long> feeds) {
    this.feeds = feeds;
  }
}
