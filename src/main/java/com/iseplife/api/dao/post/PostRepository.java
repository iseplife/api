package com.iseplife.api.dao.post;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.constants.PostState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor {

  List<Post> findAll();

  @Query(
    "select p from Post p " +
      "where p.feed.id = 1 or p.isPrivate = false " +
      "and p.state = ?1 " +
      "order by p.publicationDate"
  )
  Page<Post> findMainPostsByState(PostState state, Pageable pageable);

  @Query(
    "select p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.likes) as nbLikes, " +
      "size(p.thread.comments) as nbComments " +
    "from Post p " +
    "where p.feed.id = ?1 and p.state = ?2 and " +
      "(p.publicationDate > current_date or ?4 = true or p.author.id = ?3)" +
    "order by p.publicationDate desc "
  )
  Page<PostProjection> findCurrentFeedPost(Long feed, PostState state, Long loggedUser, Boolean isAdmin, Pageable pageable);

  @Query(
    "select p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes " +
    "from Post p " +
    "where p.feed = ?1 " +
    "and p.author.id = ?2 and p.state = 'DRAFT'"
  )
  Optional<PostProjection> findFeedDraft(Feed feed, Long author);

  @Query(
    "select p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes " +
    "from Post p " +
    "where p.feed = ?1 and p.isPinned = true " +
    "order by p.publicationDate desc"
  )
  List<PostProjection> findFeedPinnedPosts(Feed feed, Long loggedUser);

  @Query(
    "select p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes " +
    "from Post p " +
    "where p.feed.id in ?3 and p.author = ?1 and p.state = 'READY' and " +
      "(p.publicationDate > current_time or p.author.id = ?2)" +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findAuthorPosts(Long author_id, Long loggedUser, List<Long> authorizedFeeds, Pageable pageable);

  @Query(
    "select p from Post p " +
      "where p.embed = ?1 "
  )
  Optional<Post> findByEmbed(Embedable embed);
}
