package com.iseplife.api.dto.event.view;

import com.iseplife.api.dao.event.EventPreviewProjection;

import java.util.Date;
import java.util.Set;

public class EventPreview {
  private Long id;
  private String title;
  private String type;
  private Set<Long> targets;
  private Date start;
  private Date end;
  private String cover;
  private Boolean published;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public Set<Long> getTargets() {
    return targets;
  }

  public void setTargets(Set<Long> targets) {
    this.targets = targets;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }
}
