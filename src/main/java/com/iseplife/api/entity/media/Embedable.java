package com.iseplife.api.entity.media;

public interface Embedable {
  Long id = 0L;

  Long getId();
  void setId(Long id);
  String getEmbedType();
}