package com.iseplife.api.entity.subscription;

import java.util.List;

public interface Subscribable {
  Long getId();
  List<Subscription> getSubscriptions();
}
