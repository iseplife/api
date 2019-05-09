package com.iseplive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.entity.user.Student;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
  private Post post;

  @ManyToOne
  private Student student;

  private Date creation;

  @Column(length = 500)
  private String message;

  @ManyToMany
  private Set<Student> like = new HashSet<>();

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
  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  @JsonIgnore
  public Set<Student> getLike() {
    return like;
  }

  public void setLike(Set<Student> like) {
    this.like = like;
  }

  public int getLikes() {
    return like.size();
  }
}
