package com.iseplife.api.dao;

import com.iseplife.api.entity.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  Subscription findByFeedIdAndListenerId(Long id, Long listenerID);
}

