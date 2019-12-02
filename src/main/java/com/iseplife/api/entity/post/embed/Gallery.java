package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.media.Embed;
import com.iseplife.api.entity.media.Image;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Gallery implements Embed {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private Boolean official = false;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "gallery")
  private List<Image> images = new ArrayList<>();

  private Date creation;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonIgnore
  public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Image getCoverImage() {
    return images.size() > 0 ? images.get(0) : null;
  }

  public List<Image> getPreviewImages() {
    return images.subList(0, Math.min(images.size(), 10));
  }

  public Boolean getOfficial() { return official; }

  public void setOfficial(Boolean official) { this.official = official; }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Date getCreation() {
    return creation;
  }

  public String getEmbedType(){
    return EmbedType.GALLERY;
  }
}
