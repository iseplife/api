package com.iseplife.api.entity.subscription;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Student;

import javax.persistence.*;

@Entity
public class Subscription {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student listener;

  @ManyToOne
  private Feed feed;


  public Long getId(){
    return id;
  }

  public Feed getFeed() {
    return feed;
  }

  public void setFeed(Feed feed) {
    this.feed = feed;
  }

  public Student getListener() {
    return listener;
  }

  public void setListener(Student listener) {
    this.listener = listener;
  }
}
