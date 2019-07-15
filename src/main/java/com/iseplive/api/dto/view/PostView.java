package com.iseplive.api.dto.view;

import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.Media;
import com.iseplive.api.entity.user.Author;
import com.iseplive.api.entity.user.Student;

import java.util.Date;

/**
 * Created by Guillaume on 13/08/2017.
 * back
 */
public class PostView {
  private Long id;
  private String title;
  private Date creationDate;
  private Date publicationDate;
  private String description;
  private Media media;
  private Student author;
  private Club linkedClub;
  private Integer nbLikes;
  private Boolean isLiked;
  private Integer nbComments;
  private Boolean isPinned;
  private Boolean isPrivate;
  private Boolean hasWriteAccess;

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

  public Date getPublicationDate() { return publicationDate; }

  public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Media getMedia() {
    return media;
  }

  public void setMedia(Media media) { this.media = media; }

  public Student getAuthor() {
    return author;
  }

  public void setAuthor(Student author) {
    this.author = author;
  }

  public Integer getNbLikes() {
    return nbLikes;
  }

  public void setNbLikes(Integer nbLikes) {
    this.nbLikes = nbLikes;
  }

  public Boolean getLiked() {
    return isLiked;
  }

  public void setLiked(Boolean liked) {
    isLiked = liked;
  }

  public Boolean getHasWriteAccess() {
    return hasWriteAccess;
  }

  public void setHasWriteAccess(Boolean hasWriteAccess) {
    this.hasWriteAccess = hasWriteAccess;
  }

  public Boolean getPinned() {
    return isPinned;
  }

  public void setPinned(Boolean pinned) {
    isPinned = pinned;
  }

  public Integer getNbComments() {
    return nbComments;
  }

  public void setNbComments(Integer nbComments) {
    this.nbComments = nbComments;
  }

  public Boolean getPrivate() { return isPrivate;  }

  public void setPrivate(Boolean isPrivate) { this.isPrivate = isPrivate;  }

  public Club getLinkedClub() { return linkedClub; }

  public void setLinkedClub(Club linkedClub) { this.linkedClub = linkedClub; }

}
