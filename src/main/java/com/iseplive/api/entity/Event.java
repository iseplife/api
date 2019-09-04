package com.iseplive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.MediaType;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.Media;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(MediaType.EVENT)
public class Event extends Media {

  private String title;
  private String location;
  private Date date;

  @Column(columnDefinition = "TEXT")
  private String description;

  @JsonIgnore
  @OneToOne
  private Feed feed;

  @ManyToOne
  private Feed target;

  @ManyToOne
  private Club club;

  private String imageUrl;

  @Override
  public void setCreation(Date creation) {
    super.setCreation(creation);
  }

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
}
