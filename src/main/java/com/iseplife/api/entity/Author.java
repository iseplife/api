package com.iseplife.api.entity;

import java.util.Date;

public interface Author {

  Long getId();
  Date getMediaCooldown();
  void setMediaCooldown(Date date);
  Integer getMediaCounter();
  void setMediaCounter(Integer counter);
}
