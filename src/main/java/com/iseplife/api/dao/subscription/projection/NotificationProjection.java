package com.iseplife.api.dao.subscription.projection;

import java.util.Date;
import java.util.Map;


public interface NotificationProjection {
  Long getId();
  
  String getType();
  String getIcon();
  String getLink();
  
  Map<String, Object> getInformations();
  
  Date getCreation();
}
