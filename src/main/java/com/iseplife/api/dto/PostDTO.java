package com.iseplife.api.dto;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
public class PostDTO {
  private String feed;
  private String description;
  private Long linkedClubId;
  private Boolean isPrivate;
  private Boolean isDraft;

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

  public Long getLinkedClubId() { return linkedClubId; }

  public void setLinkedClubId(Long linkedClubId) { this.linkedClubId = linkedClubId; }

  public Boolean getDraft() { return isDraft; }

  public void setDraft(Boolean draft) { this.isDraft = draft; }

  public String getFeed() {
    return feed;
  }

  public void setFeed(String feed) {
    this.feed = feed;
  }
}
