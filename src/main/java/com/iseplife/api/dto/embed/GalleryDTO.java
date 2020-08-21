package com.iseplife.api.dto.embed;

import java.util.List;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
public class GalleryDTO {
  private String name;
  private Boolean pseudo = true;
  private List<Long> images;
  private Long club;
  private Long feed;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getPseudo() {
    return pseudo;
  }

  public void setPseudo(Boolean pseudo) {
    this.pseudo = pseudo;
  }

  public List<Long> getImages() {
    return images;
  }

  public void setImages(List<Long> images) {
    this.images = images;
  }

  public Long getClub() {
    return club;
  }

  public void setClub(Long club) {
    this.club = club;
  }

  public Long getFeed() {
    return feed;
  }

  public void setFeed(Long feed) {
    this.feed = feed;
  }
}
