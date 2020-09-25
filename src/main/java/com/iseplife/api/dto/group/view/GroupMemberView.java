package com.iseplife.api.dto.group.view;

import com.iseplife.api.dto.student.view.StudentPreview;

public class GroupMemberView {
  private Long id;
  private StudentPreview student;
  private Boolean admin;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public StudentPreview getStudent() {
    return student;
  }

  public void setStudent(StudentPreview student) {
    this.student = student;
  }

  public Boolean getAdmin() {
    return admin;
  }

  public void setAdmin(Boolean admin) {
    this.admin = admin;
  }
}
