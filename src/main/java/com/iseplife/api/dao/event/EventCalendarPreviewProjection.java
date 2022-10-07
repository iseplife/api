package com.iseplife.api.dao.event;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.Set;

public interface EventCalendarPreviewProjection {
  Long getId();
  String getTitle();
  String getType();
  Date getStartsAt();
  Date getEndsAt();

  @Value("#{target.targets.![id]}")
  Set<Long> getTargets();
}
