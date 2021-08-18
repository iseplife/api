package com.iseplife.api.dto.embed.view.media;

public class VideoView extends MediaView {
  private String title;
  private Integer views;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getViews() {
    return views;
  }

  public void setViews(Integer views) {
    this.views = views;
  }
}
