package com.iseplive.api.dto;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
public class PostDTO {
  private String title;
  private String content;
  private Long linkedClubId;
  private Boolean isPrivate;
  private Boolean draft;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public Long getLinkedClubId() { return linkedClubId; }

  public void setLinkedClubId(Long linkedClubId) { this.linkedClubId = linkedClubId; }

  public Boolean isDraft() { return draft; }

  public void setDraft(Boolean draft) { this.draft = draft; }
}
