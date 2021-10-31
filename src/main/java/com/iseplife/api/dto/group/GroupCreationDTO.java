package com.iseplife.api.dto.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupCreationDTO {
  private String name;
  private boolean restricted;
  private List<Long> admins;
}
