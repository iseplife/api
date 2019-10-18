package com.iseplife.api.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.media.Media;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
public class Post {

  @Id
  @GeneratedValue
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;

  private Date publicationDate;
  private Date creationDate; //TODO: remove creation date ? Not useful anymore
  private Boolean isPrivate = false;
  private Boolean isPinned = false;

  @OneToOne(cascade = CascadeType.ALL)
  private Media media;

  @ManyToOne
  private Student author;

  @ManyToOne
  private Club linkedClub = null;

  @JsonIgnore
  @ManyToOne
  private Feed feed;

  @JsonIgnore
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private PublishStateEnum publishState;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Student getAuthor() {
    return author;
  }

  public void setAuthor(Student author) {
    this.author = author;
  }

  public PublishStateEnum getPublishState() {
    return publishState;
  }

  public void setPublishState(PublishStateEnum publishState) {
    this.publishState = publishState;
  }

  public Media getMedia() {
    return media;
  }

  public void setMedia(Media media) {
    this.media = media;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  @JsonIgnore
  public List<Like> getLikes() { return likes; }

  public void setLikes(List<Like> likes) {
    this.likes = likes;
  }

  public Boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public Boolean getPinned() {
    return isPinned;
  }

  public void setPinned(Boolean pinned) {
    isPinned = pinned;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public Club getLinkedClub() {
    return linkedClub;
  }

  public void setLinkedClub(Club linkedClub) {
    this.linkedClub = linkedClub;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }
}
