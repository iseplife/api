package com.iseplife.api.entity.wei;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiRoomMember {
  @Id
  private WeiRoom room;

  @Column(unique = true)
  @OneToOne()
  @Id
  private Student student;
  
  private boolean admin;
  
  private Date joined = new Date();
}
