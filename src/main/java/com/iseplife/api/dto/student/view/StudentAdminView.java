package com.iseplife.api.dto.student.view;

import java.util.Date;
import java.util.List;

public class StudentAdminView {
  private Long id;

  private boolean isArchived;
  private Integer promo;
  private String picture;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;

  private String phoneNumber;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private List<String> roles;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public String getTwitter() {
    return twitter;
  }

  public void setTwitter(String twitter) {
    this.twitter = twitter;
  }

  public String getInstagram() {
    return instagram;
  }

  public void setInstagram(String instagram) {
    this.instagram = instagram;
  }

  public String getSnapchat() {
    return snapchat;
  }

  public void setSnapchat(String snapchat) {
    this.snapchat = snapchat;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setArchived(boolean archived) {
    isArchived = archived;
  }
}
