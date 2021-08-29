package com.iseplife.api.dao.post;

import com.iseplife.api.constants.AuthorType;

public interface AuthorProjection {
  Long getId();
  String getName();
  AuthorType getAuthorType();
  String getThumbnail();
}
