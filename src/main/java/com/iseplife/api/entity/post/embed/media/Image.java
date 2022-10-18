package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.face.FaceMatch;

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

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Thread thread;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Matched> matched;
  
  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<FaceMatch> faceMatchs;

  public String getEmbedType(){
    return EmbedType.IMAGE;
  }
}
