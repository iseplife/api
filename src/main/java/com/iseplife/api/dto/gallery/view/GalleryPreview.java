package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.entity.post.embed.media.Image;

import java.util.List;

public class GalleryPreview {
  private Long id;
  private String name;
  private List<Image> preview;

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

  public List<Image> getPreview() {
    return preview;
  }

  public void setPreview(List<Image> preview) {
    this.preview = preview;
  }
}
