package com.iseplife.api.dto.post.view;

import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.dto.view.AuthorView;

import java.util.Date;

/**
 * Created by Guillaume on 13/08/2017.
 * back
 */
public class PostView {
  private Long id;
  private Long thread;

  private Date publicationDate;
  private String description;
  private EmbedView embed;
  private AuthorView author;

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

  public Date getPublicationDate() { return publicationDate; }

  public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EmbedView getEmbed() {
    return embed;
  }

  public void setEmbed(EmbedView embed) { this.embed = embed; }



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
}
