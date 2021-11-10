package com.iseplife.api.dto.club.view;

import java.util.Date;

import com.iseplife.api.constants.ClubType;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;

import lombok.Data;

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
  private SubscriptionProjection subscribed;
  private String website;
  private String facebook;
  private String instagram;
}
