package com.iseplive.api.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplive.api.entity.user.Student;

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
  private Post post;

  @ManyToOne
  private Student student;

  private Date creation;

  @ManyToOne
  private Comment parent;

  @Column(length = 500)
  private String message;

  @OneToMany(cascade = CascadeType.REMOVE)
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
