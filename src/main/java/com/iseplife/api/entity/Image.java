package com.iseplife.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.media.Gallery;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
public class Image {
  @Id
  @GeneratedValue
  private Long id;
  private String thumbUrl;
  private String fullSizeUrl;
  private String originalUrl;

  @ManyToOne
  @JsonIgnore
  private Gallery gallery;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
  private List<Matched> matched;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id;}

  public List<Matched> getMatched() {
    return matched;
  }

  public void setMatched(List<Matched> matched) {
    this.matched = matched;
  }

  public String getFullSizeUrl() {
    return fullSizeUrl;
  }

  public void setFullSizeUrl(String fullSizeUrl) {
    this.fullSizeUrl = fullSizeUrl;
  }

  public String getThumbUrl() {
    return thumbUrl;
  }

  public void setThumbUrl(String thumbUrl) {
    this.thumbUrl = thumbUrl;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
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
}
