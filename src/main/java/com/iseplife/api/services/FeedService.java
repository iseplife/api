package com.iseplife.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.FeedProjection;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.exceptions.http.HttpNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
  @Lazy final private PostService postService;
  @Lazy final private StudentService studentService;
  final private FeedRepository feedRepository;

  public Feed getFeed(Long id) {
    Optional<Feed> feed = feedRepository.findById(id);
    if (feed.isEmpty() || !SecurityService.hasReadAccess(feed.get()))
      throw new HttpNotFoundException("feed_not_found");

    return feed.get();
  }

  public Iterable<FeedProjection> getUserFeeds(TokenPayload token) {
    return feedRepository.findAllByIdIn(token.getFeeds());
  }

  @Cacheable("main-posts")
  public Page<PostProjection> getMainFeedPosts(int page) {
    return postService.getFeedPosts(1L, page);
  }

  public Page<PostProjection> getFeedPosts(Long id, int page) {
    return postService.getFeedPosts(id, page);
  }

  public List<PostProjection> getFeedPostsPinned(Long id) {
    Feed feed = getFeed(id);
    return postService.getFeedPostsPinned(feed);
  }

  public PostProjection getFeedDrafts(Long id, Long author) {
    Feed feed = getFeed(id);
    return postService.getFeedDrafts(feed, author);
  }
}
