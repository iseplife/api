package com.iseplife.api.dto.student.view;

import java.util.List;

public class StudentPreviewAdmin extends StudentPreview {
  private List<String> roles;
  private boolean isArchived;


  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setArchived(boolean archived) {
    isArchived = archived;
  }
}
