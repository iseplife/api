package com.iseplife.api.entity;

import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

@Entity
public class GroupMember {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student student;

  private Boolean admin;

  @ManyToOne
  private Group group;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Boolean isAdmin() {
    return admin;
  }

  public void setAdmin(Boolean admin) {
    this.admin = admin;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }
}
