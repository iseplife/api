package com.iseplife.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "`group`")
public class Group {
  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private GroupType type = GroupType.DEFAULT;

  @Column(unique = true)
  private String name;

  private Boolean restricted = false;

  private Date archivedAt;

  private String cover;

  @OneToOne(cascade = CascadeType.ALL)
  private Feed feed;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Student> admins;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean isRestricted() {
    return restricted;
  }

  public void setRestricted(Boolean restricted) {
    this.restricted = restricted;
  }

  public Boolean isArchived() {
    return archivedAt != null;
  }

  public void setArchivedAt(Date archivedAt) {
    this.archivedAt = archivedAt;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  public List<Student> getAdmins() {
    return admins;
  }

  public void setAdmins(List<Student> admins) {
    this.admins = admins;
  }

  public GroupType getType() {
    return type;
  }

  public void setType(GroupType type) {
    this.type = type;
  }
}
