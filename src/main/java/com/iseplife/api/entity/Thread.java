package com.iseplife.api.entity;

import com.iseplife.api.entity.feed.Feed;
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

  @OneToOne(mappedBy = "thread", cascade = CascadeType.ALL)
  private Post post;

  @OneToOne(mappedBy = "thread", cascade = CascadeType.ALL)
  private Comment comment;

  @OneToMany(mappedBy = "parentThread", cascade = CascadeType.ALL)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public List<Like> getLikes() {
    return likes;
  }

  public void setLikes(List<Like> likes) {
    this.likes = likes;
  }

  public Post getPost() {
    return post;
  }

  {
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

  public Feed getFeed() {
    if (post != null) {
      return post.getFeed();
    } else {
      return comment.getParentThread().getFeed();
    }

  }

}
