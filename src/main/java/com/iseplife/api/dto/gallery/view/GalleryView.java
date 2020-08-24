package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.entity.post.embed.media.Image;

import java.util.Date;
import java.util.List;

public class GalleryView {
  private Long id;
  private String name;
  private Date creation;
  private List<Image> images;
  private ClubPreview club;
  private Boolean hasRight;

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

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }

  public ClubPreview getClub() {
    return club;
  }

  public void setClub(ClubPreview club) {
    this.club = club;
  }

  public Boolean getHasRight() {
    return hasRight;
  }

  public void setHasRight(Boolean hasRight) {
    this.hasRight = hasRight;
  }
}
