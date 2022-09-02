package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface CommentSimpleProjection {
  @Value("#{target.id}")
  Long getId();
  @Value("#{target.creation}")
  Date getCreation();
  @Value("#{target.message}")
  String getMessage();
  @Value("#{target.lastEdition}")
  Date getLastEdition();

  @Value("#{target.asClub == null ? target.student: target.asClub}")
  AuthorProjection getAuthor();
}
