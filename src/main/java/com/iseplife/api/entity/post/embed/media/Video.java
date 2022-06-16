package com.iseplife.api.entity.post.embed.media;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue(MediaType.VIDEO)
@Getter @Setter @NoArgsConstructor
public class Video extends Media {
  private String title;
  private Double ratio;

  private Integer views = 0;

  public String getEmbedType(){
    return EmbedType.VIDEO;
  }
}
