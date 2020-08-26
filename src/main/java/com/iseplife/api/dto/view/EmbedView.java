package com.iseplife.api.dto.view;

import com.iseplife.api.constants.EmbedType;

import java.util.List;

public class EmbedView {

  Long id;
  String type;
  List<String> links;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }


}
