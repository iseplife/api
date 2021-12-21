package com.iseplife.api.entity.survey;

import com.iseplife.api.entity.feed.Feed;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Survey {

  @Id
  @GeneratedValue
  private Long id;

  private Boolean enabled = false;

  private String title;

  private Date opensAt;

  private Date closesAt;

  private Boolean anonymous;

  private Boolean multiple;

  @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
  private Set<SurveyChoice> choices;

  @ManyToMany
  private Set<Feed> targets;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getOpensAt() {
    return opensAt;
  }

  public void setOpensAt(Date opensAt) {
    this.opensAt = opensAt;
  }

  public Date getClosesAt() {
    return closesAt;
  }

  public void setClosesAt(Date closesAt) {
    this.closesAt = closesAt;
  }

  public Boolean getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(Boolean anonymous) {
    this.anonymous = anonymous;
  }

  public Boolean getMultiple() {
    return multiple;
  }

  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  public Set<SurveyChoice> getChoices() {
    return choices;
  }

  public void setChoices(Set<SurveyChoice> choices) {
    this.choices = choices;
  }

  public Set<Feed> getTargets() {
    return targets;
  }

  public void setTargets(Set<Feed> targets) {
    this.targets = targets;
  }
}
