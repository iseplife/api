package com.iseplife.api.dao.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PostState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  List<Post> findAll();

  Page<Post> findByFeedAndStateOrderByPublicationDate(Feed feed, PostState state, Pageable pageable);


  List<Post> findByFeedAndStateOrderByPublicationDate(Feed feed, PostState state);

  @Query(
    "select p from Post p "+
      "where p.feed = ?1 " +
      "and p.author = ?2 " +
      "and p.state = 'DRAFT'"
  )
  List<Post> findFeedDrafts(Feed feed, Student author);

  List<Post> findByFeedAndIsPinnedIsTrue(Feed feed);

  @Query(value = "select * from post as p where embed_id = ?1 and embed_type = ?2", nativeQuery = true)
  List<Post> findAllByEmbed(Long id, String type);

  Page<Post> findByAuthorIdOrderByCreationDateDesc(Long author_id, Pageable pageable);

  Page<Post> findByAuthorIdAndIsPrivateOrderByCreationDateDesc(Long author_id, Boolean isPrivate, Pageable pageable);
}
