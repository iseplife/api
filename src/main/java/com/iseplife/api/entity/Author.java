package com.iseplife.api.entity;

import com.iseplife.api.constants.AuthorType;

import java.util.Date;

public interface Author {
  Long getId();
  String getName();
  AuthorType getAuthorType();
  String getThumbnail();
  Date getMediaCooldown();
  void setMediaCooldown(Date date);
  Integer getMediaCounter();
  void setMediaCounter(Integer counter);
}
