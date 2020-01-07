package com.iseplife.api.dao.event;

import com.iseplife.api.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
  List<Event> findAll();

  @Query(
    "select e from Event e " +
      "where (?2 = true or e.target.name in (?1)) " +
      "and e.startsAt <= ?3 and (e.visible = true or ?2 = true) order by e.startsAt"
  )
  Page<Event> findPassedEvents(List<String> feed, Boolean admin, Date date, Pageable pageable);


  @Query(
    value =
      "(select e.* from event e join feed f on (e.target_id = f.id and (?2 = 1 or f.name in ?3)) where (e.visible = 1 or ?2 = 1) and date(e.starts_at) = ?1) union " +
      "(select * from event t where t.starts_at > date_add(?1, interval 1 day) and (t.visible = 1 or ?2 = 1) and (?2 = true or t.target_id in (?3)) limit 10) union " +
      "(select * from event y where y.starts_at < date_add(?1, interval -1 day) and (y.visible = 1 or ?2 = 1) and (?2 = true or y.target_id in (?3)) limit 10)"
    , nativeQuery = true
  )
  List<Event> findAroundDate(Date date, Boolean admin, List<String> feeds);


  @Query(
    "select e from Event e " +
      "where (?2 = true or e.target.name in (?1)) " +
      "and e.startsAt >= ?3  and (e.visible = true or ?2 = true) order by e.startsAt"
  )
  Page<Event> findFutureEvents(List<String> feed, Boolean admin, Date date, Pageable pageable);

  @Query(
    "select e from Event e " +
      "where lower(e.title) like %?1% "
  )
  List<Event> searchEvent(String name);
}
