package com.iseplife.api.entity.subscription;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;

import org.jose4j.json.internal.json_simple.JSONObject;

import com.iseplife.api.entity.user.Student;
import com.iseplife.api.utils.JpaConverterJson;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @RequiredArgsConstructor
@NoArgsConstructor
public class Notification {
  @Id
  @GeneratedValue
  private Long id;
  
  @NonNull
  private String type, icon, link;

  @NonNull
  @Convert(converter = JpaConverterJson.class)
  private Map<String, Object> informations;
  
  
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Student> students;
  
  private Date creation;
  
  private boolean watched;
  
  @PrePersist
  protected void onCreate() {
    creation = new Date();
  }
  
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
