package com.iseplive.api.services;

import com.google.common.base.CaseFormat;
import com.iseplive.api.dao.feed.FeedRepository;
import com.iseplive.api.dao.post.PostRepository;
import com.iseplive.api.entity.Feed;
import com.iseplive.api.entity.post.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private PostRepository postRepository;

  public Feed getMain() {
    return feedRepository.findMain();
  }

  public List<Post> getMainPosts() {
    return
      feedRepository
      .findMain()
      .getPosts();
  }

  public List<Post> getFeedPosts(String name) {
    return
      feedRepository
      .findFeedByName(name.toUpperCase())
      .getPosts();
  }

  public List<Post> getFeedPostsPinned(String name) {
    Feed feed = feedRepository.findFeedByName(name);
    return
      postRepository.findByFeedAndIsPinnedIsTrue(feed);
  }

  public Feed createFeed(String name) {
    Feed feed = new Feed();
    feed.setName(name);

    feedRepository.save(feed);
    return feed;
  }
}
