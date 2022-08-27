package com.iseplife.api.dao.firebase;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.entity.subscription.FirebaseSubscription;
import com.iseplife.api.entity.user.Student;

@Repository
public interface FirebaseSubscriptionRepository extends CrudRepository<FirebaseSubscription, Long> {
  Optional<FirebaseSubscription> findByTokenOrFingerprint(String token, String fingerprint);
  
  @Transactional
  @Modifying
  @Query("update FirebaseSubscription s set s.lastUpdate = :lastUpdate, s.owner = :student where s.id = :id")
  void updateDateAndOwner(Long id, Date lastUpdate, Student student);
}