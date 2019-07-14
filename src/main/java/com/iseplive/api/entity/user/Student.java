package com.iseplive.api.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
public class Student implements UserDetails {

  @Id
  @Column(unique = true)
  private Long id;

  private Integer promo;
  private Date archivedAt = null;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;
  private Boolean recognition;

  @Column(unique = true)
  private String phone_id;
  @Column(unique = true)
  private String phone_number;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private Boolean allowNotifications = false;

  private String photoUrl;
  private String photoUrlThumb;

  @Column(length = 300) // TODO: Potential removal
  private String bio;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getPromo() {
    return promo;
  }

  public void setPromo(Integer promo) { this.promo = promo;  }

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

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPhoneNumber() {
    return phone_number;
  }

  public void setPhoneNumber(String phone_number) {
    this.phone_number = phone_number;
  }

  public String getPhoneId() {
    return phone_id;
  }

  public void setPhoneId(String phone_id) {
    this.phone_id = phone_id;
  }

  public Boolean getRecognition() { return recognition;  }

  public void setRecognition(Boolean recognition) { this.recognition = recognition; }

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

  public boolean isArchived() { return archivedAt != null; }

  public void setArchivedAt(Date archivedAt) {
    this.archivedAt = archivedAt;
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
