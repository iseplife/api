package com.iseplife.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Thread {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  private Post post;

  @OneToOne(cascade = CascadeType.ALL)
  private Comment comment;

  @JsonIgnore
  @OneToMany(mappedBy = "parentThread", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  @JsonIgnore
  public List<Like> getLikes() { return likes; }

  public void setLikes(List<Like> likes) {
    this.likes = likes;
  }

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

}
