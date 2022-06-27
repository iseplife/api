package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Gallery;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(MediaType.IMAGE)
@Getter @Setter @NoArgsConstructor
public class Image extends Media {
  private String color;
  private Float ratio;
  
  @ManyToOne
  private Gallery gallery;

  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
  private List<Matched> matched;

  public String getEmbedType(){
    return EmbedType.IMAGE;
  }
}
