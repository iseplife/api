package com.iseplife.api.dto.view;


import com.iseplife.api.dao.post.CommentProjection;

import java.util.Date;

public class CommentView implements CommentProjection {
  private Long id;
  private Long thread;
  private AuthorView author;
  private Date creation;
  private String message;
  private Integer likes;
  private Integer comments;
  private Boolean liked;
  private Date lastEdition;
  private Boolean hasWriteAccess;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getThread() {
    return thread;
  }

  public void setThread(Long thread) {
    this.thread = thread;
  }

  public AuthorView getAuthor() {
    return author;
  }

  public void setAuthor(AuthorView author) {
    this.author = author;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getLikes() {
    return likes;
  }

  public void setLikes(Integer likes) {
    this.likes = likes;
  }

  public Integer getComments() {
    return comments;
  }

  public void setComments(Integer comments) {
    this.comments = comments;
  }

  public Boolean getLiked() {
    return liked;
  }

  public void setLiked(Boolean liked) {
    this.liked = liked;
  }

  public Date getLastEdition() {
    return lastEdition;
  }

  public void setLastEdition(Date lastEdition) {
    this.lastEdition = lastEdition;
  }

  public Boolean getHasWriteAccess() {
    return hasWriteAccess;
  }

  public void setHasWriteAccess(Boolean hasWriteAccess) {
    this.hasWriteAccess = hasWriteAccess;
  }
}
