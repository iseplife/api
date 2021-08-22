package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.dto.embed.view.media.ImageView;

import java.util.List;

public class PseudoGalleryView extends EmbedView {
  private Long id;
  private List<ImageView> images;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

 public List<ImageView> getImages() {
    return images;
  }

  public void setImages(List<ImageView> images) {
    this.images = images;
  }
}
