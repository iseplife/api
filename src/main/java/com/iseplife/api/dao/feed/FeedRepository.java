package com.iseplife.api.dao.feed;

import com.iseplife.api.entity.feed.Feed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends CrudRepository<Feed, Long> {
  List<Feed> findAll();
}
