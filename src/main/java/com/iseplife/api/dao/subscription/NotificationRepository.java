package com.iseplife.api.dao.subscription;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.subscription.Notification;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
}
