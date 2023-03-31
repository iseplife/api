package com.iseplife.api.entity.club;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.constants.ClubType;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.subscription.Subscribable;

import com.iseplife.api.services.ClubService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter @Setter @NoArgsConstructor
public class Club implements Feedable, Author, Subscribable {
  
  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  private ClubType type;

  @Column(length = ClubService.MAX_DESCRIPTION_LENGTH)
  private String description;

  private String logoUrl;
  private String coverUrl;

  private Date archivedAt = null;
  private Date creation;

  private String facebook;
  private String snapchat;
  private String instagram;
  private String website;

  private Integer mediaCounter;
  private Date mediaCooldown;

  @Column(columnDefinition = "boolean default true")
  private Boolean viewable = true;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Feed feed;

  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<ClubMember> members;

  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Event> events;

  @OneToMany(mappedBy = "linkedClub", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Post> posts;

  @Override
  public AuthorType getAuthorType() {
    return AuthorType.CLUB;
  }

  @Override
  public String getThumbnail() {
    return logoUrl;
  }

  public boolean isArchived() {
    return !(archivedAt == null || archivedAt.getTime() > new Date().getTime());
  }
}
