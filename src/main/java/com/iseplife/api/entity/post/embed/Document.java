package com.iseplife.api.entity.post.embed;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.media.Embed;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(EmbedType.DOCUMENT)
public class Document extends Embed {
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
