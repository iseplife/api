package com.iseplife.api.dao.post;

import java.util.Date;
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
    "select distinct " +
      "p as post, " +
      "t.id as thread, " +
      "size(t.comments) as nbComments, " +
      "size(l) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join Student s on s.id = :loggedUser " +
      "join s.subscriptions subs " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where (p.feed.id = subs.subscribedFeed.id or p.homepageForced = true) " +
      "and p.state = 'READY' " +
      "and p.homepagePinned = false " +
      "and (p.publicationDate <= now() or p.author.id = :loggedUser) " +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findHomepagePosts(Long loggedUser, Pageable pageable);

  @Query(
    "select distinct " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
      "from Post p " +
        "join Student s on s.id = :loggedUser " +
        "join s.subscriptions subs " +
        "join p.thread t " +
        "left join t.likes l " +
        "left join l.student likeStudent on likeStudent.id = :loggedUser " +
      "where (p.feed.id = subs.subscribedFeed.id or p.homepageForced = true) " +
        "and p.state = 'READY' " +
        "and p.homepagePinned = true " +
        "and (p.publicationDate <= now() or p.author.id = :loggedUser) " +
      "group by p, t " +
      "order by p.publicationDate desc"
  )
  List<PostProjection> findHomepagePostsPinned(Long loggedUser);

  @Query(
    "select distinct " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join Student s on s.id = :loggedUser " +
      "join s.subscriptions subs " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.publicationDate <= :lastDate and (p.feed.id = subs.subscribedFeed.id or p.homepageForced = true) " +
      "and p.state = 'READY' " +
      "and p.homepagePinned = false " +
      "and (p.publicationDate <= now() or p.author.id = :loggedUser) " +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findPreviousHomepagePosts(Long loggedUser, Date lastDate, Pageable pageable);

  @Query(
    "select distinct " +
      "p as post, " +
      "t.id as thread, " +
      "size(t.comments) as nbComments, " +
      "size(l) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.state = 'READY' " +
      "and p.linkedClub is not NULL " +
      "and p.publicationDate <= now() " +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findExplorePosts(Long loggedUser, Pageable pageable);

  @Query(
    "select distinct " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.publicationDate <= :lastDate " +
      "and p.state = 'READY' " +
      "and p.linkedClub is not NULL " +
      "and p.publicationDate <= now() " +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findPreviousExplorePosts(Long loggedUser, Date lastDate, Pageable pageable);

  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.feed.id = :feed and p.state = :state and p.pinned = false " +
      "and (p.publicationDate <= now() or :isAdmin = true or p.author.id = :loggedUser) " +
    "group by p, t"
  )
  Page<PostProjection> findCurrentFeedPost(Long feed, PostState state, Long loggedUser, Boolean isAdmin, Pageable pageable);
  @Query(
      "select " +
        "p as post, " +
        "p.thread.id as thread, " +
        "size(p.thread.comments) as nbComments, " +
        "size(p.thread.likes) as nbLikes, " +
        "count(likeStudent) > 0 as liked " +
      "from Post p " +
        "join p.thread t " +
        "left join t.likes l " +
        "left join l.student likeStudent on likeStudent.id = :loggedUser " +
      "where p.publicationDate < :lastDate and p.feed.id = :feed and p.state = :state and p.pinned = false " +
        "and (p.publicationDate <= now() or :isAdmin = true or p.author.id = :loggedUser) " +
      "group by p, t"
    )
    Page<PostProjection> findPreviousCurrentFeedPost(Long feed, Date lastDate, PostState state, Long loggedUser, Boolean isAdmin, Pageable pageable);

  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.feed = :feed and p.pinned = true " +
      "and (p.publicationDate <= now() or :isAdmin = true or p.author.id = :loggedUser) " +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  List<PostProjection> findFeedPinnedPosts(Feed feed, Long loggedUser, Boolean isAdmin);


  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p join p.thread as t " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.feed = :feed " +
    "and p.author.id = :author and p.state = 'DRAFT' " +
    "group by p, t"
  )
  Optional<PostProjection> findFeedDraft(Feed feed, Long loggedUser, Long author);


  @Query(
    "select " +
      "p as post, " +
      "p.thread.id as thread, " +
      "size(p.thread.comments) as nbComments, " +
      "size(p.thread.likes) as nbLikes, " +
      "count(likeStudent) > 0 as liked " +
    "from Post p " +
      "join p.thread t " +
      "left join t.likes l " +
      "left join l.student likeStudent on likeStudent.id = :loggedUser " +
    "where p.feed.id in :authorizedFeeds and p.author = :author_id and p.state = 'READY' and " +
      "(p.publicationDate <= now() or p.author.id = :loggedUser)" +
    "group by p, t " +
    "order by p.publicationDate desc"
  )
  Page<PostProjection> findAuthorPosts(Long author_id, Long loggedUser, List<Long> authorizedFeeds, Pageable pageable);

  @Query(
    "select p from Post p where p.embed = ?1 "
  )
  Optional<Post> findByEmbed(Embedable embed);
  
  @Query(
      "select " +
        "p as post, " +
        "p.thread.id as thread, " +
        "size(p.thread.comments) as nbComments, " +
        "size(p.thread.likes) as nbLikes, " +
        "count(likeStudent) > 0 as liked " +
      "from Post p " +
        "join p.thread t " +
        "left join t.likes l " +
        "left join l.student likeStudent on likeStudent.id = :loggedUser " +
      "where p.id = :id " +
      "group by p, t"
  )
  PostProjection getById(Long id, Long loggedUser);

  @Query(
      "select " +
        "p as post, " +
        "p.thread.id as thread, " +
        "size(p.thread.comments) as nbComments, " +
        "size(p.thread.likes) as nbLikes, " +
        "count(likeStudent) > 0 as liked " +
      "from Post p " +
        "join p.thread t " +
        "left join t.likes l " +
        "left join l.student likeStudent on likeStudent.id = :loggedUser " +
      "where p.feed.id = :feedId and p.id = :id " +
      "group by p, t"
  )
  PostProjection findByFeedIdAndId(Long feedId, Long loggedUser, Long id);
  
  @Query(
      "select " +
        "p.id " +
       "from Post p " +
       "where p.embed = :embed"
  )
  Long findPostIdByEmbed(Embedable embed);
}
