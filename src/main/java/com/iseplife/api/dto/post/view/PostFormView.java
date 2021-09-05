package com.iseplife.api.dto.post.view;

import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.dto.view.AuthorView;

import java.util.Date;

public class PostFormView {
  private Long id;
  private Long thread;

  private Date publicationDate;
  private String description;
  private EmbedView embed;
  private AuthorView author;

  private Boolean isPinned;
  private Boolean isPrivate;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EmbedView getEmbed() {
    return embed;
  }

  public void setEmbed(EmbedView embed) {
    this.embed = embed;
  }

  public AuthorView getAuthor() {
    return author;
  }

  public void setAuthor(AuthorView author) {
    this.author = author;
  }

  public Boolean getPinned() {
    return isPinned;
  }

  public void setPinned(Boolean pinned) {
    isPinned = pinned;
  }

  public Boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public Long getThread() {
    return thread;
  }

  public void setThread(Long thread) {
    this.thread = thread;
  }
}
