package com.iseplife.api.entity.event;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.constants.EventType;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
  private String coordinates;
  private String ticketUrl = null;
  private Float price = null;

  private Date publishedAt = new Date();
  private boolean closed = false;

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
  
  @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Subscription> subscriptions;
}
