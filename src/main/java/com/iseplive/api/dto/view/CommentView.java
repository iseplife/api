package com.iseplive.api.dto.view;

import com.iseplive.api.entity.Post;
import com.iseplive.api.entity.user.Student;

import java.util.Date;
import java.util.Set;

/**
 * Created by Guillaume on 16/08/2017.
 * back
 */
public class CommentView {
  private Long id;
  private Student student;
  private Date creation;
  private String message;
  private Set<Student> likes;
  private boolean isLiked;

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

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Set<Student> getLikes() {
    return likes;
  }

  public void setLikes(Set<Student> likes) {
    this.likes = likes;
  }

  public boolean isLiked() {
    return isLiked;
  }

  public void setLiked(boolean liked) {
    isLiked = liked;
  }
}
