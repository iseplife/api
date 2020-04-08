package com.iseplife.api.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.Subscription;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Student implements UserDetails {

  @Id
  @Column(unique = true)
  private Long id;

  @NotBlank(message = "Promo is mandatory")
  private Integer promo;
  private Date archivedAt;

  @NotBlank(message = "firstName is mandatory")
  private String firstName;
  @NotBlank(message = "lastName is mandatory")
  private String lastName;
  private String mail;
  private Date birthDate;

  @JsonIgnore
  private Boolean recognition;

  @JsonIgnore
  @Column(unique = true)
  private String phoneId;
  @Column(unique = true)
  private String phoneNumber;

  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  @JsonIgnore
  private Boolean allowNotifications = false;

  private String picture;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;

  @JsonIgnore
  @OneToMany(mappedBy = "listener", cascade = CascadeType.ALL)
  private List<Subscription> subscriptions;

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
    return phoneNumber;
  }

  public void setPhoneNumber(String phone_number) { this.phoneNumber = phone_number; }

  public String getPhoneId() {
    return phoneId;
  }

  public void setPhoneId(String phoneId) {
    this.phoneId = phoneId;
  }

  public Boolean getRecognition() { return recognition;  }

  public void setRecognition(Boolean recognition) { this.recognition = recognition; }

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


  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public void addSubscription(Subscription subscription){
    this.subscriptions.add(subscription);
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }
}
