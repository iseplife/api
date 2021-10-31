package com.iseplife.api.dto.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupUpdateDTO {
  private String name;
  private boolean restricted;
  private List<Long> admins;
  private Boolean resetCover = false;
}
