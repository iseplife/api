package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.media.Embed;
import com.iseplife.api.entity.media.Image;
import com.iseplife.api.entity.media.Media;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@DiscriminatorValue(EmbedType.GALLERY)
public class Gallery extends Embed {

  private String name;

  private Boolean official = false;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "gallery")
  private List<Image> images = new ArrayList<>();

  @Override
  public void setCreation(Date creation) {
    super.setCreation(creation);
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
}
