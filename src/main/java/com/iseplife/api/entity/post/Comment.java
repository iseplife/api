package com.iseplife.api.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Entity
public class Comment {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Thread thread;

  @ManyToOne
  private Student student;

  private Date creation;

  @ManyToOne
  private Comment parent;

  @Column(length = 500)
  private String message;

  @OneToMany(mappedBy = "comment" ,cascade = CascadeType.REMOVE,  orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

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

  @JsonIgnore
  public Thread getThread() {
    return thread;
  }

  public void setThread(Thread thread) {
    this.thread = thread;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public void setLikes(List<Like> likes) {
    this.likes = likes;
  }

  public List<Like> getLikes() {
    return likes;
  }

  public Comment getParent() {
    return parent;
  }

  public void setParent(Comment parent) {
    this.parent = parent;
  }
}
