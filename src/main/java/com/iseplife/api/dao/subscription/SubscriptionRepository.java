package com.iseplife.api.dao.subscription;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  @Query("select case when (count(s) > 0) then true else false end "
      + "from Subscription s where s.listener.id = ?2 and s.subscribed.id = ?1")
  Boolean existsSubscriptionBySubscribedIdAndListenerId(Long id, Long listenerID);

  @Query("select s from Subscription s where " +
    "s.listener.id = ?2 and s.subscribed.id = ?1")
  Subscription findBySubscribedIdAndListenerId(Long id, Long listenerID);

  @Query("select s.subscribedFeed.id from Subscription s where " +
    "s.listener.id = ?1")
  List<Long> findFeedsByListenerId(Long listenerID);

  @Query("select s.subscribedFeed.id from Subscription s where " +
    "s.listener.id = ?2 and s.subscribed.id = ?1")
  Long findFeedBySubscribedIdAndListenerId(Long id, Long listenerID);

  @Query(
    "select " +
      "case when count(sub)> 0 then true else false end " +
    "from Subscription sub where " +
      "sub.listener.id = :listener and " +
      "sub.subscribed = :sub"
  )
  boolean existsBySubscribedAndListener_Id(Subscribable sub, Long listener);

  @Query("select s from Subscription s where " +
      "s.listener.id = ?2 and s.subscribed.id = ?1")
  SubscriptionProjection findProjectionBySubscribedIdAndListenerId(Long id, Long listenerID);

  @Query("select s from Subscription s where " +
      "s.listener.id = ?2 and s.subscribed = ?1")
  SubscriptionProjection findProjectionBySubscribedAndListenerId(Subscribable subable, Long listenerID);

  @Query("select s from Subscription s where " +
      "s.subscribed.id = ?1")
  List<Subscription> findBySubscribedId(Long id);

  @Query("select distinct s from Subscription s " +
      "join fetch s.listener listener " +
      "left join fetch listener.firebaseSubscriptions " +
      "where s.subscribed = ?1")
  List<Subscription> findBySubscribed(Subscribable subable);

  @Transactional
  @Modifying
  @Query("delete from Subscription s where " +
    "s.listener.id = ?2 and s.subscribed.id = ?1")
  void deleteBySubscribedIdAndListenerId(Long id, Long listenerID);

}

