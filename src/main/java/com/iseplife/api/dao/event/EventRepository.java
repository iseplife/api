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
      "where e.target in ?1 " +
      "and e.endsAt < ?3 and (e.visible = true or ?2 = true)"
  )
  Page<Event> findPassedEvents(List<Long> feed, Boolean admin, Date date, Pageable pageable);

  @Query(
    value = "select * from Event " +
      "where target in ?1 " +
      "and (visible = true or ?2 = true) " +
      "and ( trunc(starts_at) = ?3 " +
        "or id in (select id from Event where startsAt > dateadd(DAY, 1, ?3) limit 10) " +
        "or id in (select id from Event where startsAt < dateadd(DAY, -1, ?3)) limit 10) "  +
      "order by starts_at desc"
    , nativeQuery = true
  )
  List<Event> findAroundDate(List<Long> feed, Boolean admin, Date date);

  @Query(
    "select e from Event e " +
      "where e.target in ?1" +
      "and e.startsAt > ?3  and (e.visible = true or ?2 = true) "
  )
  Page<Event> findFutureEvents(List<Long> feed, Boolean admin, Date date, Pageable pageable);

  @Query(
    "select e from Event e " +
      "where lower(e.title) like %?1% "
  )
  List<Event> searchEvent(String name);
}
