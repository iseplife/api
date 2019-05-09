package com.iseplive.api.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.AuthorTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
@DiscriminatorValue(AuthorTypes.STUDENT)
public class Student extends Author implements UserDetails {

  private Integer promo;

  private String firstname;
  private String lastname;

  private Date birthDate;

  private String phone;
  private String address;

  @Column(unique = true)
  private String studentId;

  private String mail;
  private String mailISEP;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private Boolean allowNotifications = false;

  private String photoUrl;
  private String photoUrlThumb;

  @Column(length = 300) // TODO Pas boquant ??
  private String bio;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;

  public Integer getPromo() {
    return promo;
  }

  public void setPromo(Integer promo) {
    this.promo = promo;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
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

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }


  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return null;
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return null;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

  @JsonIgnore
  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getMailISEP() {
    return mailISEP;
  }

  public void setMailISEP(String mailISEP) {
    this.mailISEP = mailISEP;
  }

  public String getFacebook() {
    return facebook;
  }

  public void setFacebook(String facebook) {
    this.facebook = facebook;
  }

  public String getInstagram() {
    return instagram;
  }

  public void setInstagram(String instagram) {
    this.instagram = instagram;
  }

  public String getTwitter() {
    return twitter;
  }

  public void setTwitter(String twitter) {
    this.twitter = twitter;
  }

  public String getPhotoUrlThumb() {
    return photoUrlThumb;
  }

  public void setPhotoUrlThumb(String photoUrlThumb) {
    this.photoUrlThumb = photoUrlThumb;
  }

  public Boolean getAllowNotifications() {
    return allowNotifications;
  }

  public void setAllowNotifications(Boolean allowNotifications) {
    this.allowNotifications = allowNotifications;
  }

  public String getSnapchat() {
    return snapchat;
  }

  public void setSnapchat(String snapchat) {
    this.snapchat = snapchat;
  }
}
