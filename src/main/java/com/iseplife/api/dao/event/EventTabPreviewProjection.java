package com.iseplife.api.dao.event;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.entity.post.embed.Gallery;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface EventTabPreviewProjection {
  Long getId();
  @Value("#{target.feed.id}")
  Long getFeedId();
  String getTitle();
  String getDescription();
  String getType();
  Date getStartsAt();
  Date getEndsAt();

  @Value("#{target.feed.galleries}")
  List<Gallery> getGalleries();

  @Value("#{target.publishedAt.before(new java.util.Date())}")
  boolean isPublished();

  @Value("#{target.targets.![id]}")
  Set<Long> getTargets();
}
