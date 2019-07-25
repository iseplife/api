package com.iseplive.api.entity.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.constants.ClubTypesEnum;
import com.iseplive.api.entity.Post;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 27/07/2017.
 * back
 */
@Entity
public class Club {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  @Column(length = 500)
  private String description;

  private Date archivedAt = null;
  private Date createdAt;

  @Enumerated(EnumType.STRING)
  private ClubTypesEnum type;

  private String facebook;
  private String facebook_token;
  private String snapchat;
  private String instagram;
  private String website;
  // true if it is iseplive's club.
  private Boolean isAdmin;

  @JsonIgnore
  @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClubMember> members;

  @JsonIgnore
  @OneToMany(mappedBy = "linkedClub", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  private String logoUrl;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isArchived() {
    return archivedAt != null;
  }

  public void setArchivedAt(Date archivedAt) {
    this.archivedAt = archivedAt;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public List<ClubMember> getMembers() {
    return members;
  }

  public void setMembers(List<ClubMember> members) {
    this.members = members;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public Boolean getAdmin() {
    return isAdmin;
  }

  public void setAdmin(Boolean admin) {
    isAdmin = admin;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public String getFacebook() { return facebook; }

  public void setFacebook(String facebook) { this.facebook = facebook; }

  public String getFacebook_token() { return facebook_token; }

  public void setFacebook_token(String facebook_token) { this.facebook_token = facebook_token; }

  public String getSnapchat() { return snapchat; }

  public void setSnapchat(String snapchat) { this.snapchat = snapchat; }

  public String getInstagram() { return instagram; }

  public void setInstagram(String instagram) { this.instagram = instagram; }

  public ClubTypesEnum getType() { return type; }

  public void setType(ClubTypesEnum type) { this.type = type; }
}
