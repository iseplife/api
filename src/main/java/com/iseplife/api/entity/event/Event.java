package com.iseplife.api.entity.event;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.iseplife.api.constants.EventType;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.subscription.Subscribable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Event implements Feedable, Subscribable {
  @Id
  @GeneratedValue
  private Long id;

  private String title;
  private String cover;

  @Enumerated(EnumType.STRING)
  private EventType type;

  private Date startsAt;
  private Date endsAt;

  private String location;
  @ManyToOne
  private EventPosition position;
  
  private String ticketUrl = null;
  private Float price = null;

  private Date publishedAt = new Date();

  @Column(columnDefinition = "TEXT")
  private String description;

  //An event can have event child in specific cases (e.g BDE Campaign)
  @OneToMany(cascade = CascadeType.ALL)
  private List<Event> children;

  @ManyToOne
  private Club club;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Feed feed;

  @ManyToMany
  private Set<Feed> targets;

  @OneToOne
  private Event previousEdition;
}
