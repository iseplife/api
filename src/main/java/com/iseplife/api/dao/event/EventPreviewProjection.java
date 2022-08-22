package com.iseplife.api.dao.event;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.dao.club.projection.ClubPreviewProjection;

import java.util.Date;
import java.util.Set;

public interface EventPreviewProjection {
  Long getId();
  @Value("#{target.feed.id}")
  Long getFeedId();
  String getTitle();
  String getType();
  Date getStartsAt();
  Date getEndsAt();
  
  String getDescription();
  
  ClubPreviewProjection getClub();

  @Value("#{target.publishedAt.before(new java.util.Date())}")
  boolean isPublished();

  @Value("#{target.targets.![id]}")
  Set<Long> getTargets();
}
