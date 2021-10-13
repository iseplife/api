package com.iseplife.api.dao.webpush;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.subscription.WebPushSubscription;

@Repository
public interface WebPushSubscriptionRepository extends CrudRepository<WebPushSubscription, Long> {

  Optional<WebPushSubscription> findByAuthAndKeyAndEndpoint(String auth, String key, String endpoint);
}

