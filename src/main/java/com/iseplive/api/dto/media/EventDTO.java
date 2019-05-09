package com.iseplive.api.dto.media;

import java.util.Date;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class EventDTO {
  private String title;
  private String location;
  private Date date;
  private String description;
  private Long clubId;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getClubId() {
    return clubId;
  }

  public void setClubId(Long clubId) {
    this.clubId = clubId;
  }

}
