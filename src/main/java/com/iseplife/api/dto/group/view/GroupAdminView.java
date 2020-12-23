package com.iseplife.api.dto.group.view;

import java.util.List;

public class GroupAdminView {
  private Long id;
  private String name;
  private Boolean restricted;
  private Boolean archived;
  private Boolean locked;
  private String cover;
  private List<GroupMemberView> admins;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  public List<GroupMemberView> getAdmins() {
    return admins;
  }

  public void setAdmins(List<GroupMemberView> admins) {
    this.admins = admins;
  }
}
