package com.iseplive.api.entity.media;

import com.iseplive.api.constants.MediaType;

import javax.persistence.*;

@Entity
@DiscriminatorValue(MediaType.STATUS)
public class Status extends Media {

  @Column(columnDefinition = "TEXT")
  private String description;

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }
}
