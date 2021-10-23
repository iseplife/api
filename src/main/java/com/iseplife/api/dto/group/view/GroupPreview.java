package com.iseplife.api.dto.group.view;

import lombok.Data;

@Data
public class GroupPreview {
  private Long id;
  private String name;
  private boolean restricted;
  private boolean archived;
}
