package com.iseplife.api.entity.post.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.media.Image;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Gallery implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private Boolean pseudo = false;

  private Date creation;

  @JsonIgnore
  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "gallery")
  private List<Image> images = new ArrayList<>();

  @JsonIgnore
  @ManyToOne
  private Feed feed;

  @JsonIgnore
  @ManyToOne
  private Club club;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Image> getPreview() {
    return images.subList(0, Math.min(images.size(), 5));
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Date getCreation() {
    return creation;
  }

  public String getEmbedType(){
    return EmbedType.GALLERY;
  }

  public Club getClub() {
    return club;
  }

  public void setClub(Club club) {
    this.club = club;
  }

  public Feed getFeed(){
    return feed;
  }

  public void setFeed(Feed feed){
    this.feed = feed;
  }

  public Boolean getPseudo() {
    return pseudo;
  }

  public void setPseudo(Boolean pseudo) {
    this.pseudo = pseudo;
  }
}
