package com.iseplife.api.dao.event;

import org.springframework.beans.factory.annotation.Value;
import java.util.Date;
import java.util.Set;

public interface EventPreviewProjection {
  Long getId();
  String getTitle();
  String getType();
  Date getStartsAt();
  Date getEndsAt();

  @Value("#{target.publishedAt.before(new java.util.Date())}")
  Boolean getPublished();

  @Value("#{target.targets.![id]}")
  Set<Long> getTargets();
}
