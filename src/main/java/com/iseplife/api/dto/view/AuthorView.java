package com.iseplife.api.dto.view;

import com.iseplife.api.constants.AuthorType;

public class AuthorView {
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

  public AuthorType getType() {
    return type;
  }

  public void setType(AuthorType type) {
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
