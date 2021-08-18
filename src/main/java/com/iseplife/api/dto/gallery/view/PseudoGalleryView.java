package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.post.view.EmbedView;
import com.iseplife.api.entity.post.embed.media.Image;

import java.util.List;

public class PseudoGalleryView extends EmbedView {
  private Long id;
  private List<Image> images;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

 public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }
}
