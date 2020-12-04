package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iseplife.api.constants.EmbedType;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Embedable {
  Long getId();
  String getEmbedType();
}
