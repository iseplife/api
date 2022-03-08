package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface CommentProjection {
  @Value("#{target.id}")
  Long getId();
  Date getCreation();
  String getMessage();
  Date getLastEdition();

  @Value("#{target.asClub == null ? target.student: target.asClub}")
  AuthorProjection getAuthor();

  
  Boolean getLiked();
  @Value("#{target.thread.id}")
  Long getThread();
  @Value("#{target.thread.likes.size}")
  Integer getLikes();
  @Value("#{target.thread.comments.size}")
  Integer getComments();
}
