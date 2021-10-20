package com.iseplife.api.entity.survey;

import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

@Entity
public class SurveyVote {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student voter;

  @OneToOne
  private SurveyChoice choice;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
