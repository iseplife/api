package com.iseplife.api.dto.gallery;

import java.util.List;


public class GalleryDTO {
  private String name;
  private String description;
  private Boolean pseudo = true;
  private Boolean generatePost = false;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getGeneratePost() {
    return generatePost;
  }

  public void setGeneratePost(Boolean generatePost) {
    this.generatePost = generatePost;
  }
}
