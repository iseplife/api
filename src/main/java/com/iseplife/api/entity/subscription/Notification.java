package com.iseplife.api.entity.subscription;

import java.util.HashMap;
import java.util.Map;

import org.jose4j.json.internal.json_simple.JSONObject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Notification {
  final private String type, icon, link;
  final private Map<String, Object> informations;
  public String getPayload() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("type", type);
    map.put("icon", icon);
    map.put("link", link);
    map.put("informations", informations);
    return new JSONObject(map).toString();
  }
  
  
  public static class NotificationType {
    public static String NEW_EVENT = "new.event";
    public static String NEW_GROUP_POST = "new.group.post";
    public static String NEW_CLUB_POST = "new.club.post";
  }
}
