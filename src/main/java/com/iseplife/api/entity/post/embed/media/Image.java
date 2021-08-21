package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Gallery;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(MediaType.IMAGE)
public class Image extends Media {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Gallery gallery;

  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
  private List<Matched> matched;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<Matched> getMatched() {
    return matched;
  }

  public void setMatched(List<Matched> matched) {
    this.matched = matched;
  }

  public Gallery getGallery() {
    return gallery;
  }

  public void setGallery(Gallery gallery) {
    this.gallery = gallery;
  }

  public Thread getThread() {
    return thread;
  }

  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public String getEmbedType(){
    return EmbedType.IMAGE;
  }

}
