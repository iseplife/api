package com.iseplife.api.dao.club.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ClubPreviewProjection {
  Long getId();
  String getName();
  @Value("#{target.feed.id}")
  Long getFeedId();
  String getDescription();
  String getLogoUrl();
}
