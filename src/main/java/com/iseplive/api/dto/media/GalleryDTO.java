package com.iseplive.api.dto.media;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class GalleryDTO {
  private String name;
  private Date creation;
  private List<Long> images;

  public List<Long> getImages() {
    return images;
  }

  public void setImages(List<Long> images) {
    this.images = images;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }
}
