package com.iseplife.api.dao.gallery;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.dao.event.EventPreviewProjection;
import com.iseplife.api.entity.post.embed.Gallery;

public interface EventGalleryProjection {
  @Value("#{target.event}")
  EventPreviewProjection getEvent();
  @Value("#{target.gallery}")
  Gallery getGallery();
}
