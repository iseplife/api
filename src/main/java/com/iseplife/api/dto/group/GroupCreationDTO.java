package com.iseplife.api.dto.group;

import java.util.List;

public class GroupCreationDTO {
  private String name;
  private Boolean restricted;
  private List<Long> admins;

  public List<Long> getAdmins() {
    return admins;
  }

  public void setAdmins(List<Long> admins) {
    this.admins = admins;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getRestricted() {
    return restricted;
  }

  public void setRestricted(Boolean restricted) {
    this.restricted = restricted;
  }
}
