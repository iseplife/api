package com.iseplife.api.entity.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Face {
  @Id
  @GeneratedValue
  Long id;
  
  String faceId;
  
  @ManyToOne
  Student student;
}
