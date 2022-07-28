package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.event.view.EventPreview;

import lombok.Data;

@Data
public class EventGalleryView {
  private GalleryPreview gallery;
  private EventPreview event;
}
