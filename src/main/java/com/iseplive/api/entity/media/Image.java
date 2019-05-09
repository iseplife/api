package com.iseplive.api.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.MediaType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.IMAGE)
public class Image extends Media {

  private String thumbUrl;
  private String fullSizeUrl;
  private String originalUrl;

  @ManyToOne
  @JsonIgnore
  private Gallery gallery;

  @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
  private List<Matched> matched;

  @Override
  public void setCreation(Date creation) {
    super.setCreation(creation);
  }

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
}
