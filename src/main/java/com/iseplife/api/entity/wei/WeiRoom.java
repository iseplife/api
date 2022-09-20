package com.iseplife.api.entity.wei;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity
@Getter @Setter @NoArgsConstructor
public class WeiRoom {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Type(type="uuid-char")
  @Column(name="id", columnDefinition = "VARCHAR(255)", insertable = false, updatable = false, nullable = false)
  private String id;
  
  @Id
  @Column(unique = true)
  private String roomId;
  
  private int size;
  
  @OneToMany(mappedBy = "room")
  private List<WeiRoomMember> members;
}
