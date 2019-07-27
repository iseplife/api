package com.iseplive.api.entity.post;

import com.iseplive.api.entity.user.Student;

import javax.persistence.*;

@Entity
public class Like {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Post post;

  @ManyToOne
  private Comment comment;

  @ManyToOne
  private Student student;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public Comment getComment() { return comment; }

  public void setComment(Comment comment) { this.comment = comment; }

  public Post getPost() { return post; }

  public void setPost(Post post) { this.post = post; }

  public Student getStudent() { return student; }

  public void setStudent(Student student) { this.student = student; }
}
