package com.iseplife.api.dao.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.constants.PostState;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

  List<Post> findAll();

  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "l is not null as liked, " +
      "comment as trendingComment, " +
      "lc is not null as trendingCommentLiked " +
    "from Post p " +
      "join Student s on s.id = :loggedStudent " +
      "join s.subscriptions subs " +
      "left join Like l on l.thread = p.thread and l.student.id = :loggedStudent " +
      "left join Comment comment on comment.parentThread = p.thread " +
      "left join Like lc on lc.thread = comment.thread and lc.student.id = :loggedStudent " +
    "where (p.feed.id = subs.subscribedFeed.id or p.forcedHomepage = true) " +
      "and p.state = 'READY' and p.pinned = false " +
      "and (p.publicationDate <= now() or p.author.id = :loggedStudent) " +
    "order by p.publicationDate desc, " +
    "size(comment.thread.likes) desc"
  )
  Page<PostProjection> findHomepagePosts(Long loggedStudent, Pageable pageable);
  
  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "l is not null as liked, " +
      "comment as trendingComment, " +
      "lc is not null as trendingCommentLiked " +
    "from Post p " +
      "left join Like l on l.thread = p.thread and l.student.id = :loggedStudent " +
      "left join Comment comment on comment.parentThread = p.thread " +
      "left join Like lc on lc.thread = comment.thread and lc.student.id = :loggedStudent " +
    "where p.feed.id = :feed and p.state = :state and p.pinned = false " +
      "and (p.publicationDate <= now() or :isAdmin = true or p.author.id = :loggedStudent)"
  )
  Page<PostProjection> findCurrentFeedPost(Long feed, PostState state, Long loggedStudent, Boolean isAdmin, Pageable pageable);

  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "l is not null as liked, " +
      "comment as trendingComment, " +
      "lc is not null as trendingCommentLiked " +
    "from Post p " +
      "left join Like l on l.thread = p.thread and l.student.id = :loggedStudent " +
      "left join Comment comment on comment.parentThread = p.thread " +
      "left join Like lc on lc.thread = comment.thread and lc.student.id = :loggedStudent " +
    "where p.feed = :feed and p.pinned = true " +
      "and (p.publicationDate <= now() or :isAdmin = true or p.author.id = :loggedStudent) " +
    "order by p.publicationDate desc"
  )
  List<PostProjection> findFeedPinnedPosts(Feed feed, Long loggedStudent, Boolean isAdmin);


  @Query(
    "select " +
      "p as post, " +
      "t.id as thread, " +
      "size(t.comments) as nbComments, " +
      "size(t.likes) as nbLikes " +
    "from Post p join p.thread as t " +
    "where p.feed = ?1 " +
    "and p.author.id = ?2 and p.state = 'DRAFT'"
  )
  Optional<PostProjection> findFeedDraft(Feed feed, Long author);


  @Query(
    "select p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "l is not null as liked, " +
      "comment as trendingComment, " +
      "lc is not null as trendingCommentLiked " +
    "from Post p " +
      "left join Like l on l.thread = p.thread and l.student.id = :loggedStudent " +
      "left join Comment comment on comment.parentThread = p.thread " +
      "left join Like lc on lc.thread = comment.thread and lc.student.id = :loggedStudent " +
    "where p.feed.id in :authorizedFeeds and p.author = :author_id and p.state = 'READY' and " +
      "(p.publicationDate <= now() or p.author.id = :loggedStudent)" +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findAuthorPosts(Long author_id, Long loggedStudent, List<Long> authorizedFeeds, Pageable pageable);

  @Query(
    "select p from Post p where p.embed = ?1 "
  )
  Optional<Post> findByEmbed(Embedable embed);
}
