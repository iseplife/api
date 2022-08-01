package com.iseplife.api.dto.event.view;

import com.iseplife.api.dto.gallery.view.GalleryPreview;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class EventTabPreview {
  private Long id;
  private Long feedId;
  private String title;
  private String type;
  private Set<Long> targets;
  private List<GalleryPreview> galleries;
  private Date startsAt;
  private Date endsAt;
  private boolean published;
}
