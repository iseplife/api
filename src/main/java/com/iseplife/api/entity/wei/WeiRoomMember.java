package com.iseplife.api.entity.wei;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiRoomMember {
  @Id
  private Long id;
  
  @OneToOne(fetch = FetchType.LAZY)
  private WeiRoom room;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  @MapsId
  private Student student;
  
  private boolean admin;
  
  private Date joined = new Date();
}
