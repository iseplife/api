package com.iseplife.api.dto.club.view;

import com.iseplife.api.constants.ClubType;

import java.util.Date;

public class ClubView {
  private Long id;
  private String name;
  private String description;
  private String logoUrl;
  private ClubType type;
  private Long feed;
  private Boolean archived;
  private Date creation;

  private String website;
  private String facebook;
  private String instagram;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public ClubType getType() {
    return type;
  }

  public void setType(ClubType type) {
    this.type = type;
  }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public String getInstagram() {
    return instagram;
  }

  public void setInstagram(String instagram) {
    this.instagram = instagram;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }
}
