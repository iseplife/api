package com.iseplive.api.entity;

import com.iseplive.api.constants.MediaType;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.Media;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
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

  @OneToOne
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
}
