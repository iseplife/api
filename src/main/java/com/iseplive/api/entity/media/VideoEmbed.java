package com.iseplive.api.entity.media;

import com.iseplive.api.constants.MediaType;
import com.iseplive.api.constants.VideoEmbedEnum;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.VIDEO_EMBED)
public class VideoEmbed extends Media {

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
