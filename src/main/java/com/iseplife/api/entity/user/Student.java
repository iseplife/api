package com.iseplife.api.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.Language;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.subscription.Subscription;
import com.sun.istack.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Student implements UserDetails, Author {

  @Id
  @Column(unique = true)
  private Long id;

  private Integer promo;
  private Date archivedAt;
  private Date lastConnection;

  private String firstName;
  private String lastName;
  private String mail;
  private Date birthDate;


  private String facebook;
  private String twitter;
  private String instagram;
  private String snapchat;

  private Boolean recognition = false;
  private Boolean notification = false;
  private Language language = Language.FR;

  private Integer mediaCounter;
  private Date mediaCooldown;

  @NotNull
  private Boolean hasDefaultPicture = false;

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

  public Boolean getRecognition() { return recognition;  }

  public void setRecognition(Boolean recognition) { this.recognition = recognition; }

  public boolean isArchived() { return !(archivedAt == null || archivedAt.getTime() > new Date().getTime()); }

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

  public Boolean getNotification() {
    return notification;
  }

  public void setNotification(Boolean allowNotifications) {
    this.notification = allowNotifications;
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

  @Override
  public Integer getMediaCounter() {
    return mediaCounter;
  }

  @Override
  public void setMediaCounter(Integer mediaCounter) {
    this.mediaCounter = mediaCounter;
  }

  @Override
  public Date getMediaCooldown() {
    return mediaCooldown;
  }

  @Override
  public void setMediaCooldown(Date mediaCooldown) {
    this.mediaCooldown = mediaCooldown;
  }

  public Date getLastConnection() {
    return lastConnection;
  }

  public void setLastConnection(Date lastConnection) {
    this.lastConnection = lastConnection;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public Boolean getHasDefaultPicture() {
    return hasDefaultPicture;
  }

  public void setHasDefaultPicture(Boolean hasDefaultPicture) {
    this.hasDefaultPicture = hasDefaultPicture;
  }
}
