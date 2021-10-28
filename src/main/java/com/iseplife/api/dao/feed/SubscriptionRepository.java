package com.iseplife.api.dao.feed;

import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.constants.SubscribableType;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  Boolean existsSubscriptionBySubscribedIdAndListenerId(Long id, Long listenerID);

  Feed findBySubscribedAndListenerId(Subscribable subscribable, Long listenerID);
  
  @Query("select s from Subscription s where " +
    "s.listener.id = ?1" +
    "and s.subscribed_type = '"+SubscribableType.FEED+"'" +
    "and s.subscribed_id = ?2")
  Subscription findByFeedIdAndListenerId(Long id, Long listenerID);
  
  @Query("select s.subscribed from Subscription s where " +
    "s.listener.id = ?1" +
    "and s.subscribed_type = '"+SubscribableType.FEED+"'")
  List<Feed> findAllStudentSubscribedFeed(Long student);

  /*
  @Query("select s from Subscription s where " +
    "s.listener = ?1")
  List<Subscription> findAllStudentSubscription(Student student);*/
}

