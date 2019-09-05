package com.iseplive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.MediaType;
import com.iseplive.api.entity.club.Club;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.EVENT)
public class Event {

  @Id
  @GeneratedValue
  private Long id;

  private String title;
  private Date startsAt;
  private Date endsAt;
  private String location;
  private Date creation;

  @Column(columnDefinition = "TEXT")
  private String description;

  @JsonIgnore
  @OneToOne
  private Feed feed;

  @ManyToOne
  private Feed target;

  @ManyToOne
  private Club club;

  @OneToOne
  private Event previousEdition;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

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

  public Date getStartsAt() {
    return startsAt;
  }

  public void setStartsAt(Date startsAt) {
    this.startsAt = startsAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Club getClub() {
    return club;
  }

  public void setClub(Club club) {
    this.club = club;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  public Feed getTarget() {
    return target;
  }

  public void setTarget(Feed target) {
    this.target = target;
  }

  public Date getEndsAt() { return endsAt; }

  public void setEndsAt(Date endsAt) { this.endsAt = endsAt; }

}
