package com.iseplife.api.dao.event;

import java.util.List;

import com.iseplife.api.entity.event.EventPosition;

public class PositionRequestResponse {
  public List<ResponseFeatures> features;
  public static class ResponseFeatures {
    public EventPosition properties;
  }
}
