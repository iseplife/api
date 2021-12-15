package com.iseplife.api.entity.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iseplife.api.entity.feed.Feed;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Subscribable {
  Long getId();
  Feed getFeed();
}
