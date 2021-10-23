package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.constants.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@DiscriminatorValue(MediaType.VIDEO)
@Getter @Setter @NoArgsConstructor
public class Video extends Media {
  private String title;

  private Integer views = 0;

  public String getEmbedType(){
    return EmbedType.VIDEO;
  }
}
