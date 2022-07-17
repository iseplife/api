package com.iseplife.api.dao.webpush;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.entity.subscription.WebPushSubscription;
import com.iseplife.api.entity.user.Student;

@Repository
public interface WebPushSubscriptionRepository extends CrudRepository<WebPushSubscription, Long> {
  Optional<WebPushSubscription> findByAuthAndKeyAndEndpointOrFingerprint(String auth, String key, String endpoint, String fingerprint);
  
  @Transactional
  @Modifying
  @Query("update WebPushSubscription s set s.lastUpdate = :lastUpdate, s.owner = :student where s.id = :id")
  void updateDateAndOwner(Long id, Date lastUpdate, Student student);
}