package com.iseplife.api.entity.wei.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiMapEntity {
  
  @Id
  private int id;
  
  private String name;
  private String description;
  
  @Column(columnDefinition = "text")
  private String assetUrl;

  private double lat;
  private double lng;
  
  private int size;
  
  private boolean enabled;
  
  private Date end;
}
