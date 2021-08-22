package com.iseplife.api.entity.feed;

import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Gallery;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Feed {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Event event;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Club club;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Group group;

  @ManyToMany(mappedBy = "targets")
  private Set<Event> events;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Media> media;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

  //TODO optimisation needed as it trigger SQL call
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
