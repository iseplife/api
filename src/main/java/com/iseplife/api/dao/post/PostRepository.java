package com.iseplife.api.dao.post;

import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
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

  Page<Post> findByPublishStateAndIsPinnedOrderByCreationDateDesc
    (PublishStateEnum publishState, Boolean isPinned, Pageable pageable);

  List<Post> findByPublishStateAndIsPinnedOrderByCreationDateDesc
    (PublishStateEnum publishState, Boolean isPinned);

  Page<Post> findByPublishStateAndIsPinnedAndIsPrivateOrderByCreationDateDesc
    (PublishStateEnum publishState, Boolean isPinned, Boolean isPrivate, Pageable pageable);

  List<Post> findByPublishStateAndIsPinnedAndIsPrivateOrderByCreationDateDesc
    (PublishStateEnum publishState, Boolean isPinned, Boolean isPrivate);

  Page<Post> findByAuthorIdOrderByCreationDateDesc(Long author_id, Pageable pageable);

  Page<Post> findByAuthorIdAndIsPrivateOrderByCreationDateDesc(Long author_id, Boolean isPrivate, Pageable pageable);

  List<Post> findByPublishStateAndAuthor_IdInOrderByCreationDateDesc(PublishStateEnum publishState, Collection<Long> author_ids);
}
