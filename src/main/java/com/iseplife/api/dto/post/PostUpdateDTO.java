package com.iseplife.api.dto.post;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume on 28/10/2017.
 * back
 */
public class PostUpdateDTO {
  private String description;
  private Date publicationDate;
  private Long linkedClub;
  private Boolean removeEmbed = false;
  private Map<String, Long> attachements = new HashMap<>();

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

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

  public Map<String, Long> getAttachements() {
    return attachements;
  }

  public void setAttachements(Map<String, Long> attachements) {
    this.attachements = attachements;
  }
}
