package com.iseplife.api.dto.post;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostCreationDTO {
  private Long feed;
  private String description;
  private Date publicationDate = new Date();
  private Long linkedClub = null;
  private Map<String, Long> attachements = new HashMap<>();
  private Boolean isPrivate;
  private Boolean isDraft = false;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public Long getLinkedClub() { return linkedClub; }

  public void setLinkedClub(Long linkedClub) { this.linkedClub = linkedClub; }

  public Boolean isDraft() { return isDraft; }

  public void setDraft(Boolean draft) { this.isDraft = draft; }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public Map<String, Long> getAttachements() {
    return attachements;
  }

  public void setAttachements(Map<String, Long> attachements) {
    this.attachements = attachements;
  }
}
