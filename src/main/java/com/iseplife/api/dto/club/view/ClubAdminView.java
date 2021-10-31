package com.iseplife.api.dto.club.view;

import com.iseplife.api.constants.ClubType;
import lombok.Data;

import java.util.Date;

@Data
public class ClubAdminView {
  private Long id;
  private String name;
  private String description;
  private String logoUrl;
  private String coverUrl;
  private ClubType type;
  private boolean archived;
  private Date creation;
  private String website;
  private String facebook;
  private String instagram;
}
