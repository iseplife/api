package com.iseplife.api.dto.group.view;

import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;

import lombok.Data;

@Data
public class GroupView {
  private Long id;
  private String name;
  private boolean restricted;
  private boolean archived;
  private String cover;
  private Long feedId;
  private Boolean hasRight;
  private SubscriptionProjection subscribed;
}
