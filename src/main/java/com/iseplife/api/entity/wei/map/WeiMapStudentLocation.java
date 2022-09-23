package com.iseplife.api.entity.wei.map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.*;

import com.iseplife.api.entity.user.Student;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiMapStudentLocation {
  
  @Id
  private int id;
  
  private double lat;
  private double lng;
  
  private Date timestamp;
  
  @OneToOne
  private Student student;
  
}
