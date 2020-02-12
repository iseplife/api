package com.iseplife.api.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.CommentMaxDepthException;

import javax.persistence.*;
import java.util.*;

@Entity
public class Comment implements ThreadInterface {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Thread parentThread;

  @OneToOne
  private Thread thread;

  @ManyToOne
  private Student student;

  private Date creation;

  private Date lastEdition;

  @ManyToOne
  private Comment parent;

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

  @JsonIgnore
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
    if(parentThread.getComment() == null){
      this.thread = thread;
    }else {
      throw new CommentMaxDepthException("Comment max depth reached (1)");
    }
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

  public Comment getParent() {
    return parent;
  }

  public void setParent(Comment parent) {
    this.parent = parent;
  }
}
