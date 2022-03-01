package com.iseplife.api.entity.event;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class EventPosition {
  @Id
  private String id;

  private String coordinates;
  private String location;
  
  private String label;
  private String housenumber;
  private String postcode;
  private String city;
  private String context;
  private String district;
  private String street;
}
