package com.iseplife.api.entity.feed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.Group;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Gallery;

import javax.persistence.*;
import java.util.List;

@Entity
public class Feed {

  @Id
  @GeneratedValue
  private Long id;

  @JsonIgnore
  @OneToOne(mappedBy = "feed")
  private Event event;

  @JsonIgnore
  @OneToOne(mappedBy = "feed")
  private Club club;

  @JsonIgnore
  @OneToOne(mappedBy = "feed")
  private Group group;

  @JsonIgnore
  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Post> posts;

  @JsonIgnore
  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Media> media;

  @JsonIgnore
  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Gallery> galleries;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public String getName() {
    if (club != null)
      return club.getName();
    if (event != null)
      return event.getTitle();
    if (group != null)
      return group.getName();
    return null;
  }
}
