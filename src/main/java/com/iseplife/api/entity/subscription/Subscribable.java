package com.iseplife.api.entity.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Subscribable {
  Long getId();
}
