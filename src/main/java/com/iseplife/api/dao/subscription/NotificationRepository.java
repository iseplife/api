package com.iseplife.api.dao.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.dao.subscription.projection.NotificationCountProjection;
import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.user.Student;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
  @Query(
    "select " +
      "n as notif, " +
      "case when w.id = :student then true else false end as watched " +
    "from Notification n " +
    "join n.students student on student = :student " +
    "left join n.watched w on w.id = :student"
  )
  Page<NotificationProjection> findAllByStudentsOrderById(Student student, Pageable pageable);

  @Query(
    "select " +
      "n as notif, " +
      "case when w.id = :student then true else false end as watched " +
    "from Notification n " +
    "join n.students student on student.id = :student " +
    "left join n.watched as w on w.id = :student"
  )
  Page<NotificationProjection> findAllByStudentsIdOrderById(Long student, Pageable pageable);

  NotificationProjection findProjectionById(Long id);


  @Transactional
  @Modifying
  @Query(value =
    "insert into notification_watched (watched_id, notification_id) " +
    "select ?1, id from notification " +
    "left join notification_watched " +
      "on notification_watched.watched_id = ?1 " +
      "and notification_watched.notification_id = notification.id " +
    "inner join notification_students " +
      "on notification_students.students_id = ?1 " +
      "and notification_students.notifications_id = notification.id " +
    "where id in ?2 " +
    "and notification_watched.notification_id is NULL"
    ,nativeQuery = true)
  void setWatched(Long student, Long[] ids);

  @Query(
    "select count(notif) from Notification notif " +
      "join notif.students student on student.id = :student " +
    "where student not member of notif.watched"
  )
  long countUnwatchedByStudents(Long student);

  @Query(
    "select " +
      "count(distinct case when w is null then notif else null end) as unwatched, " +
      "count(distinct notif) as count " +
    "from Notification notif " +
    "inner join notif.students s on s.id = :student " +
    "left join notif.watched w on w.id = :student"
  )
  NotificationCountProjection countUnwatchedAndAllByStudents(Long student);
}
