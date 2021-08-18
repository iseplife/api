package com.iseplife.api.dao.post;

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
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  List<Post> findAll();

  @Query(
    "select p from Post p "+
      "where p.feed.id = 1 or p.isPrivate = false " +
      "and p.state = ?1 " +
      "order by p.publicationDate"
  )
  Page<Post> findMainPostsByState(PostState state,Pageable pageable);

  Page<Post> findByFeedAndStateOrderByPublicationDateDesc(Feed feed, PostState state, Pageable pageable);


  List<Post> findByFeedAndStateOrderByPublicationDateDesc(Feed feed, PostState state);

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

  //@Query(value = "select * from post as p where embed_id =  and embed_type = ?2", nativeQuery = true)
  @Query(
    "select p from Post p "+
      "where p.embed = ?1 "
  )
  Optional<Post> findByEmbed(Embedable embed);
}
