package com.iseplife.api.dao.feed;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.Thread;

@Repository
public interface FeedRepository extends CrudRepository<Feed, Long> {

  Iterable<FeedProjection> findAllByIdIn(Iterable<Long> id);
  
  @Query("select post.feed from Post post join post.thread t on t = :thread")
  Optional<Feed> findByPostThread(Thread thread);
  
  @Query("select media.gallery.feed from Media media join media.thread t on t = :thread")
  Optional<Feed> findByMediaThread(Thread thread);

  @Transactional
  @Modifying
  @Query("update Feed f set f.lastNotification = now() where f.id = :feedId")
  void updateLastNotification(Long feedId);
}
