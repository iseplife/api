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

  @Query(value = 
    "select distinct on (p.publication_date, p.id) " +
      "p.id as p_id, " +
      "p.feed_id as feed_id, " +
      "p.publication_date as publicationDate, " +
      "p.description as description, " +
      "p.pinned as pinned, " +
      "p.thread_id as thread, " +
      "p.feed_id as feedId, " +
      "count(comment) as nbComments, " +
      "count(l) as nbLikes, " +
      "l is not null as liked, " +
      "comment.id as trendingCommentId, " +
      "comment.creation as trendingCommentCreation, " +
      "comment.message as trendingCommentMessage, " +
      "comment.thread_id as trendingCommentThreadId, " +
      "comment.last_edition as trendingCommentLastEdition, " +
      "count(comment_comment) as trendingCommentComments, " +
      "count(lc) as trendingCommentLikes, " +
      "lc is not null as trendingCommentLiked, " +
      "case when tClub is null then 'STUDENT' else 'CLUB' end as trendingCommentAuthorType, " +
      "case when tClub is null then CONCAT(tAuthor.first_name, ' ', tAuthor.last_name) else tClub.name end as trendingCommentAuthorName, " +
      "case when tClub is null then tAuthor.id else tClub.id end as trendingCommentAuthorId, " +
      "case when tClub is null then tAuthor.feed_id else tClub.feed_id end as trendingCommentAuthorFeedId, " +
      "case when tClub is null then tAuthor.picture else tClub.logo_url end as trendingCommentAuthorThumbnail, " +
      
      "case when club is null then 'STUDENT' else 'CLUB' end as authorType, " +
      "case when club is null then CONCAT(author.first_name, ' ', author.last_name) else club.name end as authorName, " +
      "case when club is null then author.id else club.id end as authorId, " +
      "case when club is null then author.feed_id else club.feed_id end as authorFeedId, " +
      "case when club is null then author.picture else club.logo_url end as authorThumbnail " +
      
    "from post as p " +
      "join subscription on subscription.listener_id = :loggedStudent " +
      "left join thread_like as l on l.thread_id = p.thread_id and l.student_id = :loggedStudent " +
      "left join thread_comment as comment on comment.parent_thread_id = p.thread_id " +
      "left join thread_comment as comment_comment on comment_comment.parent_thread_id = comment.thread_id " +
      "left join thread_like as lc on lc.thread_id = comment.thread_id and lc.student_id = :loggedStudent " +
      "left join student as author on author.id = p.author_id " +
      "left join club on club.id = p.linked_club_id " +
      "left join student as tAuthor on tAuthor.id = comment.student_id " +
      "left join club as tClub on tClub.id = comment.as_club_id " +
    "where (p.feed_id = subscription.subscribed_feed_id or p.forced_homepage = true) " +
      "and p.state = 'READY' and p.pinned = false " +
      "and (p.publication_date <= now() or p.author_id = :loggedStudent) " +
    "group by p.id, subscription.id, l.id, lc.id, comment.id, tClub.id, tAuthor.id, club.id, author.id " +
    "order by p.publication_date desc, " +
    "p.id desc, " +
    "count(lc) desc",
    nativeQuery = true
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
