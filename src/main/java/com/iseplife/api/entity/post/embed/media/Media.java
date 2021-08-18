package com.iseplife.api.entity.post.embed.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Embedable;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorColumn(name = "mediaType")
public abstract class Media implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  /**
   * We can ignore this field in json as the Embeddable interface
   * will already give use the media type by giving us the embed type
   */
  @JsonIgnore
  @Column(insertable = false, updatable = false)
  private String mediaType;

  private Boolean NSFW = false;

  private Date creation;

  private String name;

  @Enumerated(EnumType.STRING)
  private MediaStatus status;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Feed getFeed(){
    return feed;
  }

  public void setFeed(Feed feed){
    this.feed = feed;
  }

  public MediaStatus getStatus() {
    return status;
  }

  public void setStatus(MediaStatus status) {
    this.status = status;
  }
}
