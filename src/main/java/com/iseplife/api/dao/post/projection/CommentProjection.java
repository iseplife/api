package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface CommentProjection {
  @Value("#{target.comment.id}")
  Long getId();
  @Value("#{target.comment.creation}")
  Date getCreation();
  @Value("#{target.comment.message}")
  String getMessage();
  @Value("#{target.comment.lastEdition}")
  Date getLastEdition();

  @Value("#{target.comment.asClub == null ? target.comment.student: target.comment.asClub}")
  AuthorProjection getAuthor();

  boolean isLiked();
  Long getThread();
  Integer getLikes();
  Integer getComments();
}
