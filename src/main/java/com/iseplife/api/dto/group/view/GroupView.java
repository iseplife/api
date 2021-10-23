package com.iseplife.api.dto.group.view;

import lombok.Data;

@Data
public class GroupView {
  private Long id;
  private String name;
  private boolean restricted;
  private boolean archived;
  private String cover;
  private Long feed;
  private Boolean hasRight;
  private boolean subscribed;
}
