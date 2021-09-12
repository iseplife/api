package com.iseplife.api.dto.student.view;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public class StudentPreview implements StudentPreviewProjection {
  protected Long id;
  protected String firstName;
  protected String lastName;
  protected Integer promo;
  protected String picture;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Integer getPromo() {
    return promo;
  }

  public void setPromo(Integer promo) {
    this.promo = promo;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }
}
