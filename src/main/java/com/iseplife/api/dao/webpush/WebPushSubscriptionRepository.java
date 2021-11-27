package com.iseplife.api.dao.webpush;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.entity.subscription.WebPushSubscription;

@Repository
public interface WebPushSubscriptionRepository extends CrudRepository<WebPushSubscription, Long> {
  Optional<WebPushSubscription> findByAuthAndKeyAndEndpointAndOwner_IdOrFingerprint(String auth, String key, String endpoint, Long owner_id, String fingerprint);
  
  @Transactional
  @Modifying
  @Query("update WebPushSubscription s set s.lastUpdate = :lastUpdate where s.id = :id")
  void updateDate(Long id, Date lastUpdate);
}