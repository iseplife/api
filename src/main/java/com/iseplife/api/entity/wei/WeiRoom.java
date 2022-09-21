package com.iseplife.api.entity.wei;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiRoom {

  private String id = UUID.randomUUID().toString();
  
  @Id
  @Column(unique = true)
  private String roomId;
  
  private int capacity;
  
  private boolean booked = false;
  
  @OneToMany(mappedBy = "room")
  private List<WeiRoomMember> members;
  
  private Date reservedUpTo = new Date();
}
