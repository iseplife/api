package com.iseplife.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Thread {

  @JsonIgnore
  @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();


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
}
