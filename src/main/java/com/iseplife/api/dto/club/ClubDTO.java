package com.iseplife.api.dto.club;

import com.iseplife.api.constants.ClubType;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
public class ClubDTO {
  private String name;
  private String description;
  private ClubType type;

  private Date creation;

  private String website;
  private String instagram;
  private String facebook;

  private List<Long> admins;

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


  public ClubType getType() {
    return type;
  }

  public void setType(ClubType type) {
    this.type = type;
  }

  public String getInstagram() {
    return instagram;
  }

  public void setInstagram(String instagram) {
    this.instagram = instagram;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public List<Long> getAdmins() {
    return admins;
  }

  public void setAdmins(List<Long> admins) {
    this.admins = admins;
  }
}
