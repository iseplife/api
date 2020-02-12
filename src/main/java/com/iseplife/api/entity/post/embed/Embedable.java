package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Embedable {
  String getEmbedType();
}
