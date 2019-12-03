package com.iseplife.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.Post;

import javax.persistence.*;
import java.util.List;

@Entity
public class Feed {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String name;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
  private List<Post> posts;

  @JsonIgnore
  @OneToOne(mappedBy = "feed")
  private Event event;

  @JsonIgnore
  @OneToOne(mappedBy = "feed")
  private Club club;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void addPost(Post post) {
    posts.add(post);
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public Event getEvent() {
    return event;
  }

  public Club getClub() {
    return club;
  }
}
