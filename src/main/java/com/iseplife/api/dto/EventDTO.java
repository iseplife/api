package com.iseplife.api.dto;

import java.util.Date;
import java.util.Set;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class EventDTO {
  private String title;
  private String description;
  private String location;
  private Date start;
  private Date end;
  private Long clubId;
  private Long previousEditionId;
  private String ticketUrl;
  private Float price;
  private Boolean published;
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

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
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


  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
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
}
