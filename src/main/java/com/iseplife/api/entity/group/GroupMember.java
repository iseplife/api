package com.iseplife.api.entity;

import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class GroupMember {
  @Id
  @GeneratedValue
  private Long id;

  private Boolean admin;

  @ManyToOne
  private Student student;

  @ManyToOne
  private Group group;
}
