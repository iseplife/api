package com.iseplive.api.dto;

import java.util.Date;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
public class ClubDTO {
  private String name;
  private String description;
  private Date creation;
  private String website;
  private Long adminId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public Long getAdminId() {
    return adminId;
  }

  public void setAdminId(Long adminId) {
    this.adminId = adminId;
  }
}
