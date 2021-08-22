package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.dto.embed.view.media.ImageView;

import java.util.List;

public class GalleryPreview extends EmbedView {
  private Long id;
  private String name;
  private List<ImageView> preview;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ImageView> getPreview() {
    return preview;
  }

  public void setPreview(List<ImageView> preview) {
    this.preview = preview;
  }
}
