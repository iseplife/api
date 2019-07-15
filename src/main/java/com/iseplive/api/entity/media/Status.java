package com.iseplive.api.entity.media;

import javax.persistence.Column;

public class Status extends Media {

  @Column(columnDefinition = "TEXT")
  private String description;

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }
}
