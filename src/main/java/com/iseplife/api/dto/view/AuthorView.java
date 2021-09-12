package com.iseplife.api.dto.view;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.dao.post.projection.AuthorProjection;

public class AuthorView implements AuthorProjection {
    private Long id;
    private AuthorType type;
    private String name;
    private String thumbnail;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AuthorType getAuthorType() {
    return type;
  }

  public void setAuthorType(AuthorType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }
}
