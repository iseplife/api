package com.iseplife.api.dao.subscription.projection;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.constants.NotificationType;


public interface NotificationProjection {
  @Value("#{target.notif.id}")
  Long getId();

  @Value("#{target.notif.type}")
  NotificationType getType();
  @Value("#{target.notif.icon}")
  String getIcon();
  @Value("#{target.notif.link}")
  String getLink();

  @Value("#{target.watched}")
  Boolean isWatched();

  @Value("#{target.notif.informations}")
  Map<String, Object> getInformations();

  @Value("#{target.notif.creation}")
  Date getCreation();
}
