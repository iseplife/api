package com.iseplife.api.entity.survey;

import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

@Entity
public class SurveyVote {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne()
  private Student voter;

  @ManyToOne
  private SurveyChoice choice;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getVoter() {
    return voter;
  }

  public void setVoter(Student voter) {
    this.voter = voter;
  }

  public SurveyChoice getChoice() {
    return choice;
  }

  public void setChoice(SurveyChoice choice) {
    this.choice = choice;
  }
}
