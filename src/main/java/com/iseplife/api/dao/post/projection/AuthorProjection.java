package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.constants.AuthorType;

public interface AuthorProjection {
  Long getId();
  String getName();
  @Value("#{target.feed.id}")
  Long getFeedId();
  AuthorType getAuthorType();
  @Value("#{target instanceof T(com.iseplife.api.entity.club.Club) ? target.description : null}")
  String getDescription();
  String getThumbnail();
}
