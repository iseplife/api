package com.iseplife.api.entity.post;

import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "thread_comment")
public class Comment implements ThreadInterface {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Thread parentThread;

  @OneToOne(cascade = CascadeType.ALL)
  private Thread thread;

  @ManyToOne
  private Student student;

  @ManyToOne
  private Club asClub;

  private Date creation = new Date();

  private Date lastEdition;


  @Column(length = 500)
  private String message;

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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Thread getParentThread() {
    return parentThread;
  }

  public void setParentThread(Thread parentThread) {
    this.parentThread = parentThread;
  }

  @Override
  public Thread getThread() {
    return thread;
  }

  @Override
  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Date getLastEdition() {
    return lastEdition;
  }

  public void setLastEdition(Date lastEdition) {
    this.lastEdition = lastEdition;
  }

  public List<Like> getLikes() {
    return thread.getLikes();
  }

  public Club getAsClub() {
    return asClub;
  }

  public void setAsClub(Club asClub) {
    this.asClub = asClub;
  }
}
