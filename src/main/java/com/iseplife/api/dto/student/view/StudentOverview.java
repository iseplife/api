package com.iseplife.api.dto.student.view;

import com.iseplife.api.constants.FamilyType;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;

import lombok.Data;

@Data
public class StudentOverview {
  private Long id;
  private Long feedId;
  private SubscriptionProjection subscribed;
  private String picture;
  private Integer promo;
  private boolean archived;
  private String firstName;
  private String lastName;
  private String mail;
  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;
  private FamilyType family;
}
