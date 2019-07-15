package com.iseplive.api.dto.view;

import com.iseplive.api.entity.Image;
import com.iseplive.api.entity.user.Student;

/**
 * Created by Guillaume on 26/01/2018.
 * back
 */
public class MatchedView {
  private Long id;
  private Image image;
  private Student owner;
  private Long galleryId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public Student getOwner() {
    return owner;
  }

  public void setOwner(Student owner) {
    this.owner = owner;
  }

  public Long getGalleryId() {
    return galleryId;
  }

  public void setGalleryId(Long galleryId) {
    this.galleryId = galleryId;
  }
}
