package com.iseplife.api.entity.club;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.constants.ClubType;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.post.Post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
public class Club implements Feedable, Author {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  @Column(length = 500)
  private String description;
  private String logoUrl;
  private String coverUrl;

  private Date archivedAt = null;
  private Date creation;

  @Enumerated(EnumType.STRING)
  private ClubType type;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Feed feed;

  private String facebook_token;
  private String facebook;
  private String snapchat;
  private String instagram;
  private String website;

  private Integer mediaCounter;
  private Date mediaCooldown;

  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClubMember> members;

  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
  private List<Event> events;

  @OneToMany(mappedBy = "linkedClub", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getName() {
    return name;
  }

  @Override
  public AuthorType getAuthorType() {
    return AuthorType.CLUB;
  }

  @Override
  public String getThumbnail() {
    return logoUrl;
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

  public String getCoverUrl() {
    return coverUrl;
  }

  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
  }

  @Override
  public Integer getMediaCounter() {
    return mediaCounter;
  }

  @Override
  public void setMediaCounter(Integer mediaCounter) {
    this.mediaCounter = mediaCounter;
  }

  @Override
  public Date getMediaCooldown() {
    return mediaCooldown;
  }

  @Override
  public void setMediaCooldown(Date mediaCooldown) {
    this.mediaCooldown = mediaCooldown;
  }
}
