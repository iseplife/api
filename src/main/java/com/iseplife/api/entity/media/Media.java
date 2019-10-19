package com.iseplife.api.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.post.Post;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
@DiscriminatorColumn(name = "mediaType")
public abstract class Media {
  @Id
  @GeneratedValue
  private Long id;

  @Column(insertable = false, updatable = false)
  private String mediaType;

  private Boolean NSFW = false;

  @OneToOne(mappedBy = "media", cascade = CascadeType.ALL)
  private Post post;

  private Date creation;

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

  public Long getPostId() {
    return post != null ? post.getId() : 0;
  }

  @JsonIgnore
  public Post getPost() {
    return post;
  }

  public Boolean isNSFW() {
    return NSFW;
  }

  public void setNSFW(Boolean NSFW) {
    this.NSFW = NSFW;
  }
}
