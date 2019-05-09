package com.iseplive.api.dto.view;

import com.iseplive.api.entity.media.Media;
import com.iseplive.api.entity.user.Author;

import java.util.Date;

/**
 * Created by Guillaume on 13/08/2017.
 * back
 */
public class PostView {
  private Long id;
  private String title;
  private Date creationDate;
  private String content;
  private Media media;
  private Author author;
  private Integer nbLikes;
  private Boolean isLiked;
  private Integer nbComments;
  private Boolean isPinned;
  private Boolean hasWriteAccess;
  private Boolean isPrivate;

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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Media getMedia() {
    return media;
  }

  public void setMedia(Media media) {
    this.media = media;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
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
}
