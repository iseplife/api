package com.iseplife.api.dto.view;

import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.entity.feed.Feed;

import java.util.Date;
import java.util.Set;

public class EventView {
  private Long id;
  private String type;
  private String title;
  private String description;
  private String cover;

  private Date start;
  private Date end;
  private String location;
  private Float[] coordinates;
  private String ticketURL = null;
  private Float price = null;
  private Date published = new Date();
  private Boolean closed = false;
  private Boolean subscribed;
  private Boolean hasRight;

  private ClubPreview club;
  private Set<Feed> targets;
  private Long feed;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getTicketURL() {
    return ticketURL;
  }

  public void setTicketURL(String ticketURL) {
    this.ticketURL = ticketURL;
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

  public ClubPreview getClub() {
    return club;
  }

  public void setClub(ClubPreview club) {
    this.club = club;
  }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }

  public Set<Feed> getTargets() {
    return targets;
  }

  public void setTargets(Set<Feed> targets) {
    this.targets = targets;
  }

  public Boolean getSubscribed() {
    return subscribed;
  }

  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public Boolean getHasRight() {
    return hasRight;
  }

  public void setHasRight(Boolean hasRight) {
    this.hasRight = hasRight;
  }

  public Float[] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Float[] coordinates) {
    this.coordinates = coordinates;
  }
}
