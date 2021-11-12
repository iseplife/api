package com.iseplife.api.entity.subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.jose4j.json.internal.json_simple.JSONObject;

import com.iseplife.api.entity.user.Student;
import com.iseplife.api.utils.JpaConverterJson;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @RequiredArgsConstructor
public class Notification {
  @Id
  @GeneratedValue
  private Long id;
  
  @NonNull
  private String type, icon, link;

  @NonNull
  @Column(columnDefinition = "json")
  @Convert(converter = JpaConverterJson.class)
  private Map<String, Object> informations;
  
  
  @ManyToMany
  private List<Student> students;
  
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
