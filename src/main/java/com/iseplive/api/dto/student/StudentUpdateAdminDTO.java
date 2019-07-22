package com.iseplive.api.dto.student;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 03/12/2017.
 * back
 */
public class StudentUpdateAdminDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private String promo;
  private Date birthDate;
  private String phone;
  private String bio;
  private String mail;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private List<Long> roles;

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

  public String getPromo() {
    return promo;
  }

  public void setPromo(String promo) {
    this.promo = promo;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
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

  public List<Long> getRoles() {
    return roles;
  }

  public void setRoles(List<Long> roles) {
    this.roles = roles;
  }
}
