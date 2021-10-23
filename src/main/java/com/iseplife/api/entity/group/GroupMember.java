package com.iseplife.api.entity.group;

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

  private boolean admin;

  @ManyToOne
  private Student student;

  @ManyToOne
  private Group group;
}
