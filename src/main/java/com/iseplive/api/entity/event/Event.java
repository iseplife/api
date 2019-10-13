package com.iseplive.api.entity.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.EventType;
import com.iseplive.api.constants.MediaType;
import com.iseplive.api.entity.Feed;
import com.iseplive.api.entity.club.Club;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
  private String imageUrl;
  private EventType type;
  private Date startsAt;
  private Date endsAt;
  private String location;
  private Coordinates coordinates;
  private String ticketUrl = null;
  private Float price = null;

  @Column(columnDefinition = "TEXT")
  private String description;

  @OneToMany
  private List<Event> events;

  @OneToOne
  private Club club;

  @JsonIgnore
  @OneToOne
  private Feed feed;

  @ManyToOne
  private Feed target;

  @JsonIgnore
  @OneToOne
  private Event previousEdition;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }

  public void setTitle(String title) { this.title = title; }

  public String getLocation() { return location; }

  public void setLocation(String location) { this.location = location; }

  public Date getStartsAt() { return startsAt; }

  public void setStartsAt(Date startsAt) { this.startsAt = startsAt; }

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }

  public Club getClub() { return club; }

  public void setClub(Club club) { this.club = club; }

  public String getImageUrl() { return imageUrl; }

  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

  public Date getEndsAt() { return endsAt; }

  public void setEndsAt(Date endsAt) { this.endsAt = endsAt; }

  public Event getPreviousEdition() { return previousEdition; }

  public void setPreviousEdition(Event previousEdition) { this.previousEdition = previousEdition; }

  public List<Event> getEvents() {
    return events;
  }

  public void addEvent(Event event) {
    this.events.add(event);
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public EventType getType() { return type; }

  public void setType(EventType type) { this.type = type; }

  public Coordinates getCoordinates() { return coordinates; }

  public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

  public String getTicketUrl() { return ticketUrl; }

  public void setTicketUrl(String ticketUrl) { this.ticketUrl = ticketUrl; }

  public Float getPrice() { return price; }

  public void setPrice(Float price) { this.price = price; }

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
