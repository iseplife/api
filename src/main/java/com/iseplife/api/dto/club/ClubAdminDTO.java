package com.iseplife.api.dto.club;

import java.util.Date;
import java.util.List;

import com.iseplife.api.constants.ClubType;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ClubAdminDTO {
  private String name;
  private String description;
  private ClubType type;
  private Date creation;
  private String website;
  private String instagram;
  private String facebook;
  private List<Long> admins;
}
