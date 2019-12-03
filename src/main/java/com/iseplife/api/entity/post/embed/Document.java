package com.iseplife.api.entity.post.embed;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.entity.media.Media;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


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

  public String getEmbedType(){
    return EmbedType.DOCUMENT;
  }
}
