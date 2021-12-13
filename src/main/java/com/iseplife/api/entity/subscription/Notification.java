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

import com.iseplife.api.constants.NotificationType;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.utils.JpaConverterJson;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @RequiredArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @NoArgsConstructor @Builder
public class Notification {
  @Id
  @GeneratedValue
  private Long id;
  
  @NonNull
  private NotificationType type;
  
  @NonNull
  private String icon, link;

  @NonNull
  @Convert(converter = JpaConverterJson.class)
  private Map<String, Object> informations;
  
  
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Student> students;
  
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Student> watched;
  
  private Date creation;
  
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
}
