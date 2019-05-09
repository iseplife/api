package com.iseplive.api.entity.media;

import com.iseplive.api.constants.MediaType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Guillaume on 29/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.GAZETTE)
public class Gazette extends Media {

  private String title;
  private String url;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
