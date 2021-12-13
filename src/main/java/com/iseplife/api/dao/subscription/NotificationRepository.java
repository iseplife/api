package com.iseplife.api.dao.subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
  
  long countByStudentsAndWatched(Student student, Boolean watched);
}
