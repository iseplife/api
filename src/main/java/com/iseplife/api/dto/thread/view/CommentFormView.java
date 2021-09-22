package com.iseplife.api.dto.thread.view;

import com.iseplife.api.dto.view.AuthorView;

import java.util.Date;

public class CommentFormView {
  private Long id;
  private Long thread;
  private AuthorView author;
  private String message;
  private Date lastEdition;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AuthorView getAuthor() {
    return author;
  }

  public void setAuthor(AuthorView author) {
    this.author = author;
  }


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getLastEdition() {
    return lastEdition;
  }

  public void setLastEdition(Date lastEdition) {
    this.lastEdition = lastEdition;
  }

  public Long getThread() {
    return thread;
  }

  public void setThread(Long thread) {
    this.thread = thread;
  }
}
