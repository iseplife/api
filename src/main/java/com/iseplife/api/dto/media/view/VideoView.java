package com.iseplife.api.dto.media.view;

public class VideoView extends MediaView {
  private String title;
  private String thumbnail;
  private Long view;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Long getView() {
    return view;
  }

  public void setView(Long view) {
    this.view = view;
  }
}
