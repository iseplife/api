package com.iseplife.api.dto.group.view;

import lombok.Data;

import java.util.List;

@Data
public class GroupAdminView {
  private Long id;
  private String name;
  private boolean restricted;
  private boolean archived;
  private boolean locked;
  private String cover;
  private List<GroupMemberView> admins;
}
