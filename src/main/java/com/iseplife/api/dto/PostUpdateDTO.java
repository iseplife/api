package com.iseplife.api.dto;

/**
 * Created by Guillaume on 28/10/2017.
 * back
 */
public class PostUpdateDTO {
  private String title;
  private String content;
  private Boolean isPrivate = true;

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

  public Boolean getPrivate() { return isPrivate;  }

  public void setPrivate(Boolean aPrivate) {  isPrivate = aPrivate; }
}
