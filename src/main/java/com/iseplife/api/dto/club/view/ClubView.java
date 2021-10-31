package com.iseplife.api.dto.club.view;

import com.iseplife.api.constants.ClubType;
import lombok.Data;

import java.util.Date;

@Data
public class ClubView {
  private Long id;
  private String name;
  private String description;
  private String logoUrl;
  private String coverUrl;
  private ClubType type;
  private Long feed;
  private boolean archived;
  private Boolean canEdit;
  private Date creation;
  private boolean subscribed;
  private String website;
  private String facebook;
  private String instagram;
}
