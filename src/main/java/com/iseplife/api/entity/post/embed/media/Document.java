package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(MediaType.DOCUMENT)
public class Document extends Media {

  private String name;
  private String originalName;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
