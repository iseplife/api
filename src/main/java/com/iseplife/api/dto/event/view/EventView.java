package com.iseplife.api.dto.event.view;

import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.view.FeedView;

import java.util.Date;
import java.util.Set;

public class EventView {
  private Long id;
  private String type;
  private String title;
  private String description;
  private String cover;

  private Date startsAt;
  private Date endsAt;
  private String location;
  private Float[] coordinates;
  private String ticketURL = null;
  private Float price = null;
  private Date published = new Date();
  private Boolean closed = false;
  private Boolean subscribed;
  private Boolean hasRight;

  private ClubPreview club;
  private Set<FeedView> targets;
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

  public Set<FeedView> getTargets() {
    return targets;
  }

  public void setTargets(Set<FeedView> targets) {
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
