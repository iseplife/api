package com.iseplife.api.entity.media;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorColumn(name = "mediaType")
public abstract class Media implements Embed {
  @Id
  @GeneratedValue
  private Long id;

  @Column(insertable = false, updatable = false)
  private String mediaType;

  private Boolean NSFW = false;

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

  public Boolean isNSFW() {
    return NSFW;
  }

  public void setNSFW(Boolean NSFW) {
    this.NSFW = NSFW;
  }
}
