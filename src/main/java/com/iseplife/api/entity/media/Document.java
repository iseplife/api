package com.iseplife.api.entity.media;

import com.iseplife.api.constants.MediaType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by Guillaume on 25/10/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.DOCUMENT)
public class Document extends Media {
  private String name;
  private String path;
  private String originalName;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }
}
