package com.iseplife.api.entity.subscription;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;

import com.iseplife.api.constants.SubscribableType;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Student;

@Entity
public class Subscription {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student listener;

  @Any(
    fetch = FetchType.EAGER,
    metaColumn = @Column(name = "sub_type")
  )
  @AnyMetaDef(
    idType = "long",
    metaType = "string",
    metaValues = {
      @MetaValue(value = SubscribableType.CLUB, targetEntity = Club.class),
      @MetaValue(value = SubscribableType.EVENT, targetEntity = Event.class),
      @MetaValue(value = SubscribableType.FEED, targetEntity = Feed.class),
      @MetaValue(value = SubscribableType.GROUP, targetEntity = Group.class),
    }
  )
  @JoinColumn(name = "sub_id")
  private Subscribable subscribed;


  public Long getId(){
    return id;
  }

  public Subscribable getSubscribed() {
    return subscribed;
  }
  public void setSubscribed(Subscribable subscribed) {
    this.subscribed = subscribed;
  }

  public Student getListener() {
    return listener;
  }

  public void setListener(Student listener) {
    this.listener = listener;
  }
}
