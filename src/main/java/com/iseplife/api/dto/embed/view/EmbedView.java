package com.iseplife.api.dto.embed.view;


import com.iseplife.api.entity.post.embed.Embedable;

public abstract class EmbedView implements Embedable {
  String embedType;

  public String getEmbedType() {
    return embedType;
  }

  public void setEmbedType(String embedType) {
    this.embedType = embedType;
  }

}
