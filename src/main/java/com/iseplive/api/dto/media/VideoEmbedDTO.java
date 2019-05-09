package com.iseplive.api.dto.media;

import com.iseplive.api.constants.VideoEmbedEnum;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class VideoEmbedDTO {
  private VideoEmbedEnum type;
  private String url;

  public VideoEmbedEnum getType() {
    return type;
  }

  public void setType(VideoEmbedEnum type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
