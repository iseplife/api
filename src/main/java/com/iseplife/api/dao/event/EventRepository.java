package com.iseplife.api.dao.event;

import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.feed.Feed;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

  String FIND_INCOMING_EVENTS_CACHE = "findIncomingEventsCache";
  String FIND_ALL_IN_MONTH_CACHE = "findAllInMonthCache";

  // Request unoptimised as we always check if each target if it's inside user's feeds list while
  @Cacheable(cacheNames = FIND_ALL_IN_MONTH_CACHE, key = "{#date.month, #date.year, #admin, #feeds}")
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

  @Cacheable(cacheNames = FIND_INCOMING_EVENTS_CACHE, key = "{#admin, #feeds, #p}")
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
        "and (e.targets is empty or e.closed = false or t.id in ?3)" +
      ")) "
  )
  Page<Event> searchEvent(String name, Boolean admin, List<Long> feed, Pageable pageable);

  @Override
  @Caching(evict = {
    @CacheEvict(value = FIND_INCOMING_EVENTS_CACHE, allEntries = true),
    @CacheEvict(value = FIND_ALL_IN_MONTH_CACHE, allEntries = true),
    @CacheEvict(value =  PostRepository.GET_AUTHORIZED_PUBLISH_CACHE, allEntries = true)
  })
  <T extends Event> T save(T event);

  @Override
  @Caching(evict = {
    @CacheEvict(value = FIND_INCOMING_EVENTS_CACHE, allEntries = true),
    @CacheEvict(value = FIND_ALL_IN_MONTH_CACHE, allEntries = true),
    @CacheEvict(value =  PostRepository.GET_AUTHORIZED_PUBLISH_CACHE, allEntries = true)
  })
  void deleteById(Long id);
}
