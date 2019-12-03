package com.iseplife.api.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.Feed;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorColumn(name = "mediaType")
public abstract class Media implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  @Column(insertable = false, updatable = false)
  private String mediaType;

  private Boolean NSFW = false;

  private Date creation;

  @ManyToOne
  @JsonIgnore
  private Feed feed;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMediaType() {
    return mediaType;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Boolean isNSFW() {
    return NSFW;
  }

  public void setNSFW(Boolean NSFW) {
    this.NSFW = NSFW;
  }

  public Feed getFeed(){
    return feed;
  }

  public void setFeed(Feed feed){
    this.feed = feed;
  }
}
