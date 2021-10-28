package com.iseplife.api.entity.subscription;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface Subscribable {
  Long getId();
  List<Subscription> getSubscriptions();
}
