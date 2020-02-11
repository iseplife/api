package com.iseplife.api.entity.post.embed;

public interface Embedable {
  Long id = 0L;

  Long getId();
  void setId(Long id);
  String getEmbedType();
}
