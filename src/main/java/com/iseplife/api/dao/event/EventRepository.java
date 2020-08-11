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

  // Request unoptimised as we always check if each target if it's inside user's feeds list while
  @Query(
    value = "SELECT DISTINCT e.* FROM event as e " +
      "JOIN event_targets AS et JOIN feed AS f "+
      "WHERE MONTH(e.start) = MONTH(?1) and YEAR(e.start) = YEAR(?1) " +
      "AND e.id = et.event_id AND f.id = et.targets_id " +
      "AND (?2 = true || f.id in ?3) AND (?2 = true || e.published = true)" +
      "ORDER BY e.start",
    nativeQuery = true
  )
  List<Event> findAllInMonth(Date date, Boolean admin, List<Long> feeds);

  @Query(
    "select e from Event e " +
      "where (?2 = true) " +
      "and e.start <= ?3 and (e.published = true or ?2 = true) order by e.start"
  )
  Page<Event> findPassedEvents( Boolean admin, Date date, Pageable pageable);


  @Query(
    value =
      "(select e.* from event e join feed f on (e.target_id = f.id and (?2 = 1 or f.name in ?3)) where (e.published = 1 or ?2 = 1) and date(e.starts_at) = ?1) union " +
      "(select * from event t where t.starts_at > date_add(?1, interval 1 day) and (t.published = 1 or ?2 = 1) and (?2 = true or t.target_id in (?3)) limit 10) union " +
      "(select * from event y where y.starts_at < date_add(?1, interval -1 day) and (y.published = 1 or ?2 = 1) and (?2 = true or y.target_id in (?3)) limit 10 ) order by starts_at"
    , nativeQuery = true
  )
  List<Event> findAroundDate(Date date, Boolean admin);


  @Query(
    "select e from Event e " +
      "where (?2 = true) " +
      "and e.start >= ?3  and (e.published = true or ?2 = true) order by e.start"
  )
  Page<Event> findFutureEvents(Boolean admin, Date date, Pageable pageable);

  @Query(
          "select e from Event e " +
                  "where lower(e.title) like %?1% "
  )
  Page<Event> searchEvent(String name, Pageable pageable);
}
