package com.iseplive.api.entity.dor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.entity.user.Author;
import com.iseplive.api.entity.user.Student;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Entity
public class VoteDor {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JsonIgnore
  private SessionDor session;

  @ManyToOne
  @JsonIgnore
  private Student student;

  @ManyToOne
  private Author resAuthor;

  @ManyToOne
  private EventDor resEvent;

  @ManyToOne
  private QuestionDor questionDor;

  private Date date;

  private int round;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public QuestionDor getQuestionDor() {
    return questionDor;
  }

  public void setQuestionDor(QuestionDor questionDor) {
    this.questionDor = questionDor;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public SessionDor getSession() {
    return session;
  }

  public void setSession(SessionDor session) {
    this.session = session;
  }

  public int getRound() {
    return round;
  }

  public void setRound(int round) {
    this.round = round;
  }

  public Author getResAuthor() {
    return resAuthor;
  }

  public void setResAuthor(Author resAuthor) {
    this.resAuthor = resAuthor;
  }

  public EventDor getResEvent() {
    return resEvent;
  }

  public void setResEvent(EventDor resEvent) {
    this.resEvent = resEvent;
  }
}
