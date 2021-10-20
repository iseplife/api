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

  private String name;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Event event;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Club club;

  @OneToOne(mappedBy = "feed", orphanRemoval = true)
  private Group group;

  public Feed() {}
  public Feed(String name) {
    this.name = name;
  }

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

}
