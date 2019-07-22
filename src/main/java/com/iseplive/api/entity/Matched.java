package com.iseplive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.entity.user.Student;

import javax.persistence.*;

/**
 * Created by Guillaume on 02/11/2017.
 * back
 */
@Entity
public class Matched {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne
  private Student match;

  @OneToOne
  private Student owner;

  @ManyToOne
  @JsonIgnore
  private Image image;

  public Student getMatch() {
    return match;
  }

  public void setMatch(Student match) {
    this.match = match;
  }

  public Student getOwner() {
    return owner;
  }

  public void setOwner(Student owner) {
    this.owner = owner;
  }

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
}
