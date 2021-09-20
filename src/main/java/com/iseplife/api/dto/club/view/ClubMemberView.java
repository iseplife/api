package com.iseplife.api.dto.club.view;

import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.constants.ClubRole;


public class ClubMemberView implements ClubMemberProjection {
  private Long id;
  private String position;
  private ClubRole role;
  private StudentPreview student;
  private Long parent;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ClubRole getRole() {
    return role;
  }

  public void setRole(ClubRole role) {
    this.role = role;
  }

  public StudentPreview getStudent() {
    return student;
  }

  public void setStudent(StudentPreview student) {
    this.student = student;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public Long getParent() {
    return parent;
  }

  public void setParent(Long parent) {
    this.parent = parent;
  }
}
