package com.iseplife.api.dao.feed;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.subscription.Subscription;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  @Query("select case when (count(scen) > 0) then true else false end "
      + "from Subscription s where s.listener.id = ?2 and s.subscribed.id = ?1")
  Boolean existsSubscriptionBySubscribedIdAndListenerId(Long id, Long listenerID);

  @Query("select s.subscribed from Subscription s where " +
    "s.listener.id = ?2 and s.subscribed.id = ?1")
  Subscription findBySubscribedIdAndListenerId(Long id, Long listenerID);

}

