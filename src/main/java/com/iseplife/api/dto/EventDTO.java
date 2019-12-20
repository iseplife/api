package com.iseplife.api.dto;

import java.util.Date;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class EventDTO {
  private String title;
  private String location;
  private Date startsAt;
  private Date endsAt;
  private String description;
  private Long clubId;
  private Long previousEditionId;
  private String ticketUrl;
  private Float price;
  private Boolean visible;

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

  public Date getStartsAt() { return startsAt; }

  public void setStartsAt(Date startsAt) { this.startsAt = startsAt; }

  public Date getEndsAt() { return endsAt; }

  public void setEndsAt(Date endsAt) { this.endsAt = endsAt; }

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

  public Long getPreviousEditionId() {  return previousEditionId; }

  public void setPreviousEditionId(Long previousEditionId) { this.previousEditionId = previousEditionId; }

  public String getTicketUrl() { return ticketUrl; }

  public void setTicketUrl(String ticketUrl) { this.ticketUrl = ticketUrl; }

  public Float getPrice() { return price; }

  public void setPrice(Float price) { this.price = price; }

  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }
}
