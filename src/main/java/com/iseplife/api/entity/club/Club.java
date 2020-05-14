package com.iseplife.api.entity.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.ClubType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.Post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
public class Club {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  @Column(length = 500)
  private String description;
  private String logoUrl;

  private Date archivedAt = null;
  private Date creation;

  @Enumerated(EnumType.STRING)
  private ClubType type;

  @OneToOne(cascade = CascadeType.ALL)
  private Feed feed;

  @JsonIgnore
  private String facebook_token;
  private String facebook;
  private String snapchat;
  private String instagram;
  private String website;

  @JsonIgnore
  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClubMember> members;

  @JsonIgnore
  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<Event> events;

  @JsonIgnore
  @OneToMany(mappedBy = "linkedClub", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date createdAt) {
    this.creation = createdAt;
  }

  public boolean isArchived() {
    return !(archivedAt == null || archivedAt.getTime() > new Date().getTime());
  }

  public void setArchivedAt(Date archivedAt) {
    this.archivedAt = archivedAt;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public List<ClubMember> getMembers() {
    return members;
  }

  public void setMembers(List<ClubMember> members) {
    this.members = members;
  }

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public String getFacebook() { return facebook; }

  public void setFacebook(String facebook) { this.facebook = facebook; }

  public String getFacebook_token() { return facebook_token; }

  public void setFacebook_token(String facebook_token) { this.facebook_token = facebook_token; }

  public String getSnapchat() { return snapchat; }

  public void setSnapchat(String snapchat) { this.snapchat = snapchat; }

  public String getInstagram() { return instagram; }

  public void setInstagram(String instagram) { this.instagram = instagram; }

  public ClubType getType() { return type; }

  public void setType(ClubType type) { this.type = type; }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }
}
