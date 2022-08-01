package com.iseplife.api.dto.event.view;

import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.dto.club.view.ClubPreview;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class EventPreview implements EventPreviewProjection {
  private Long id;
  private Long feedId;
  private String title;
  private String type;
  private Set<Long> targets;
  private Date startsAt;
  private Date endsAt;
  private String cover;
  private boolean published;
  private ClubPreview club;
}
