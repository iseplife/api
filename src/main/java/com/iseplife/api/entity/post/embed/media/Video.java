package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.constants.MediaType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue(MediaType.VIDEO)
public class Video extends Media {
  private String title;

  private Integer views = 0;

  @Override
  public void setCreation(Date creation) {
    super.setCreation(creation);
  }

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

  public String getEmbedType(){
    return EmbedType.VIDEO;
  }

}
