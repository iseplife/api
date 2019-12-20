package com.iseplife.api.dao.post;

import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  List<Post> findAll();

  Page<Post> findByFeedAndPublishStateOrderByPublicationDate(Feed feed, PublishStateEnum state, Pageable pageable);


  List<Post> findByFeedAndPublishStateOrderByPublicationDate(Feed feed, PublishStateEnum state);

  @Query(
    "select p from Post p "+
      "where p.feed = ?1 " +
      "and p.author = ?2 " +
      "and p.publishState = 'DRAFT'"
  )
  List<Post> findFeedDrafts(Feed feed, Student author);

  List<Post> findByFeedAndIsPinnedIsTrue(Feed feed);

  Page<Post> findByAuthorIdOrderByCreationDateDesc(Long author_id, Pageable pageable);

  Page<Post> findByAuthorIdAndIsPrivateOrderByCreationDateDesc(Long author_id, Boolean isPrivate, Pageable pageable);
}