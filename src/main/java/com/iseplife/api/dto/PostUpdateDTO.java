package com.iseplife.api.dto;

import java.util.Date;

/**
 * Created by Guillaume on 28/10/2017.
 * back
 */
public class PostUpdateDTO {
  private String description;
  private Date publicationDate;
  private Boolean isPrivate = true;
  private Long linkedClub;
  private Boolean removeEmbed = false;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getPrivate() { return isPrivate;  }

  public void setPrivate(Boolean aPrivate) {  isPrivate = aPrivate; }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public Long getLinkedClub() {
    return linkedClub;
  }

  public void setLinkedClub(Long linkedClub) {
    this.linkedClub = linkedClub;
  }

  public Boolean isRemoveEmbed() {
    return removeEmbed;
  }

  public void setRemoveEmbed(Boolean removeEmbed) {
    this.removeEmbed = removeEmbed;
  }
}
