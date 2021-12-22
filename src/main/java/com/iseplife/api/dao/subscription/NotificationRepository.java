package com.iseplife.api.dao.subscription;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.user.Student;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
  @Query("select notif as notif, case when watched.id = ?1 then true else false end as watched "
      + "from Notification notif "
      + "join notif.students student on student = ?1 "
      + "left join notif.watched watched")
  Page<NotificationProjection> findAllByStudentsOrderById(Student student, Pageable pageable);
  @Query("select notif as notif, case when watched.id = ?1 then true else false end as watched "
      + "from Notification notif "
      + "join notif.students student on student.id = ?1 "
      + "left join notif.watched watched")
  Page<NotificationProjection> findAllByStudentsIdOrderById(Long student, Pageable pageable);
  

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = 
          "insert into notification_watched (watched_id, notification_id) "
        + "select ?1, id from notification "
        + "left join notification_watched "
          + "on notification_watched.watched_id = ?1 "
          + "and notification_watched.notification_id = notification.id "
        + "inner join notification_students "
          + "on notification_students.students_id = ?1 "
          + "and notification_students.notifications_id = notification.id "
        + "where id in ?2 "
        + "and notification_watched.notification_id is NULL")
  void setWatched(Long student, Long[] ids);
  
  @Query("select count(notif) "
      + "from Notification notif "
      + "join notif.students student on student.id = ?1 "
      + "where student not member of notif.watched")
  long countUnwatchedByStudents(Long student);
}
