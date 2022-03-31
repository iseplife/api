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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Subscription {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student listener;

  // Allow us to make post's related query easily without having to looking inside the any-relation
  @ManyToOne
  private Feed subscribedFeed;

  @Any(
    fetch = FetchType.EAGER,
    metaColumn = @Column(name = "subscribed_type")
  )
  @AnyMetaDef(
    idType = "long",
    metaType = "string",
    metaValues = {
      @MetaValue(value = SubscribableType.STUDENT, targetEntity = Student.class),
      @MetaValue(value = SubscribableType.CLUB, targetEntity = Club.class),
      @MetaValue(value = SubscribableType.EVENT, targetEntity = Event.class),
      @MetaValue(value = SubscribableType.GROUP, targetEntity = Group.class),
    }
  )
  @JoinColumn(name = "subscribed_id")
  private Subscribable subscribed;

  // Whether the user also want to get notification from the subscribable
  private boolean extensive;
}
