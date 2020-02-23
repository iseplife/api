package com.iseplife.api.dto;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
public class PostDTO {
  private Long feed;
  private String description;
  private Long linkedClub = null;
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

  public Long getLinkedClub() { return linkedClub; }

  public void setLinkedClub(Long linkedClub) { this.linkedClub = linkedClub; }

  public Boolean getDraft() { return isDraft; }

  public void setDraft(Boolean draft) { this.isDraft = draft; }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }
}
