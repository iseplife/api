package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Embedable {
  Long getId();
}
