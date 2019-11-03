package com.iseplife.api.dto.view;

import com.iseplife.api.constants.SearchItem;

public class SearchItemView {

  private Long id;
  private SearchItem type;
  private String name;
  private String thumbURL;
  private String description;
  private Boolean status; // Event passed, club archived, student archived


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SearchItem getType() {
    return type;
  }

  public void setType(SearchItem type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getThumbURL() {
    return thumbURL;
  }

  public void setThumbURL(String thumbURL) {
    this.thumbURL = thumbURL;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
}
