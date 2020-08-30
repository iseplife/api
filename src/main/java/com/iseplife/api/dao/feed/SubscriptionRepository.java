package com.iseplife.api.dao.feed;

import com.iseplife.api.entity.Subscription;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  Boolean existsSubscriptionByFeedIdAndListenerId(Long id, Long listenerID);

  Subscription findByFeedIdAndListenerId(Long id, Long listenerID);

  @Query("select s.feed from Subscription s where " +
    "s.listener.id = ?1")
  List<Feed> findAllStudentSubscribedFeed(Long student);

  @Query("select s.feed from Subscription s where " +
    "s.listener = ?1")
  List<Feed> findAllStudentSubscribedFeed(Student student);
}

