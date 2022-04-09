package com.iseplife.api.dao.feed;

import com.iseplife.api.constants.FeedType;

public interface FeedProjection {
  Long getId();
  String getName();
  FeedType getType();
}
