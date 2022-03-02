package com.iseplife.api.dto.event.view;

import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.view.FeedView;
import com.iseplife.api.entity.event.EventPosition;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class EventView {
  private Long id;
  private String type;
  private String title;
  private String description;
  private String cover;
  private Date startsAt;
  private Date endsAt;
  private String location;
  private EventPosition position;
  private String ticketURL = null;
  private Float price = null;
  private Date published = new Date();
  private boolean closed = false;
  private SubscriptionProjection subscribed;
  private Boolean hasRight;
  private ClubPreview club;
  private Set<FeedView> targets;
  private Long feed;
}
