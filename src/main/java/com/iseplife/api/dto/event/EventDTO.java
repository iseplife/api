package com.iseplife.api.dto.event;

import java.util.Date;
import java.util.Set;

public class EventDTO {
  private String type;
  private String title;
  private String description;
  private String location;
  private Float[] coordinates;
  private Date startsAt;
  private Date endsAt;
  private Long club;
  private Long previousEditionId;
  private String ticketUrl;
  private Float price;
  private Date published;
  private Boolean closed;
  private Set<Long> targets;

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

  public Float[] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Float[] coordinates) {
    this.coordinates = coordinates;
  }

  public Date getStartsAt() {
    return startsAt;
  }

  public void setStartsAt(Date startsAt) {
    this.startsAt = startsAt;
  }

  public Date getEndsAt() {
    return endsAt;
  }

  public void setEndsAt(Date endsAt) {
    this.endsAt = endsAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getClub() {
    return club;
  }

  public void setClub(Long club) {
    this.club = club;
  }

  public Long getPreviousEditionId() {
    return previousEditionId;
  }

  public void setPreviousEditionId(Long previousEditionId) {
    this.previousEditionId = previousEditionId;
  }

  public String getTicketUrl() {
    return ticketUrl;
  }

  public void setTicketUrl(String ticketUrl) {
    this.ticketUrl = ticketUrl;
  }

  public Float getPrice() {
    return price;
  }

  public void setPrice(Float price) {
    this.price = price;
  }


  public Date getPublished() {
    return published;
  }

  public void setPublished(Date published) {
    this.published = published;
  }

  public Boolean getClosed() {
    return closed;
  }

  public void setClosed(Boolean closed) {
    this.closed = closed;
  }

  public Set<Long> getTargets() {
    return targets;
  }

  public void setTargets(Set<Long> targets) {
    this.targets = targets;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
