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

  @OneToMany
  private Set<SurveyChoice> choices;

  @ManyToMany
  private Set<Feed> targets;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


}
