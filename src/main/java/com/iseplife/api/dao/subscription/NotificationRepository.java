package com.iseplife.api.dao.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.user.Student;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
  Page<NotificationProjection> findAllByStudentsOrderById(Student student, Pageable pageable);
  
  long countByStudentsAndWatched(Student student, Boolean watched);
}
