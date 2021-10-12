package com.iseplife.api.dao.event;

import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
  List<Event> findAll();

  // Request unoptimised as we always check if each target if it's inside user's feeds list while
  @Query(
    "select distinct e " +
      "from Event e left join e.targets t " +
      "where YEAR(e.startsAt) = YEAR(CAST(?1 as timestamp)) " +
      "and MONTH(e.startsAt) = MONTH(CAST(?1 as timestamp))" +
      "and ((?2 = true) or (" +
        "e.publishedAt < CURRENT_TIMESTAMP " +
        "and (e.targets is empty or e.closed = false or t.id in ?3)" +
      ")) " +
      "order by e.startsAt"
  )
  List<EventPreviewProjection> findAllInMonth(Date date, Boolean admin, List<Long> feeds);

  @Query(
    "select e from Event e left join e.targets t " +
      "where e.startsAt >= CURRENT_TIMESTAMP " +
      "and ((?1 = true) or (" +
        "e.publishedAt < CURRENT_TIMESTAMP " +
        "and (e.targets is empty or e.closed = false or t.id in ?2)" +
      ")) " +
      "order by e.startsAt"
  )
  Page<EventPreviewProjection> findIncomingEvents(Boolean admin, List<Long> feeds, Pageable p);

  @Query(
    "select e from Event e " +
      "where e.startsAt >= CURRENT_TIMESTAMP " +
      "and (((?1 = true) " +
        "or (e.publishedAt < CURRENT_TIMESTAMP and ?2 member of e.targets) " +
      ")) " +
      "order by e.startsAt"
  )
  Page<EventPreviewProjection> findFeedIncomingEvents(Boolean admin, Feed feed, Pageable p);

  @Query(
    "select e from Event e join e.targets t " +
      "where lower(e.title) like %?1% " +
      "and ((?2 = true) or (" +
        "e.publishedAt < CURRENT_TIMESTAMP " +
        "and (e.targets is empty or e.closed = false or t.id in ?2)" +
      ")) "
  )
  Page<Event> searchEvent(String name, Boolean admin, List<Long> feed, Pageable pageable);
}
