package com.iseplife.api.entity.media;

import com.iseplife.api.constants.MediaType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue(MediaType.VIDEO)
public class Video extends Media implements Embed {
  private String name;

  private String url;
  private String poster;

  private Integer views = 0;

  @Override
  public void setCreation(Date creation) {
    super.setCreation(creation);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getViews() {
    return views;
  }

  public void setViews(Integer views) {
    this.views = views;
  }

  public String getPoster() {
    return poster;
  }

  public void setPoster(String poster) {
    this.poster = poster;
  }
}
