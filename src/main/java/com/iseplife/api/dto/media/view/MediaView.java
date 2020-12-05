package com.iseplife.api.dto.media.view;

import com.iseplife.api.dto.post.view.EmbedView;

import java.util.Date;

public abstract class MediaView extends EmbedView {
  Long id;
  Date creation;
  String name;
  Boolean NSFW;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getNSFW() {
    return NSFW;
  }

  public void setNSFW(Boolean NSFW) {
    this.NSFW = NSFW;
  }
}
