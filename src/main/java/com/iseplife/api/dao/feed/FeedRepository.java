package com.iseplife.api.dao.feed;

import com.iseplife.api.entity.feed.Feed;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedRepository extends CrudRepository<Feed, Long> {

  String GET_FEED_CACHE = "getFeedCache";
  String GET_ALL_FEED_CACHE = "getAllFeedCache";

  @Override
  @Cacheable(cacheNames = GET_FEED_CACHE)
  Optional<Feed> findById(Long id);

  @Override
  @Cacheable(cacheNames = GET_ALL_FEED_CACHE)
  Iterable<Feed> findAllById(Iterable<Long> iterable);

  @Override
    @Caching(evict = {
      @CacheEvict(value = GET_FEED_CACHE, key = "#f.id"),
      @CacheEvict(value = GET_FEED_CACHE, allEntries = true)
    })
  <F extends Feed> F save(F F);

  @Override
  @Caching(evict = {
    @CacheEvict(value = GET_FEED_CACHE, key = "#feed.id"),
    @CacheEvict(value = GET_ALL_FEED_CACHE, allEntries = true)
  })
  void delete(Feed feed);
  Iterable<FeedProjection> findAllByIdIn(Iterable<Long> id);
}
