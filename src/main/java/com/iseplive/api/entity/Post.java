package com.iseplive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.PublishStateEnum;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.Media;
import com.iseplive.api.entity.user.Student;

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
  private Date creationDate;
  private Boolean isPrivate = false;
  private Boolean isPinned = false;

  @OneToOne(cascade = CascadeType.ALL)
  private Media media;

  @ManyToOne
  private Student author;
  @ManyToOne
  private Club linkedClub = null;

  @JsonIgnore
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  @ManyToMany
  private Set<Student> like = new HashSet<>();

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

  public Set<Student> getLike() {
    return like;
  }

  public void setLike(Set<Student> like) {
    this.like = like;
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
}
