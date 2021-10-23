package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.constants.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(MediaType.DOCUMENT)
@Getter @Setter @NoArgsConstructor
public class Document extends Media {
  private String title;
  public String getEmbedType(){
    return EmbedType.DOCUMENT;
  }
}
