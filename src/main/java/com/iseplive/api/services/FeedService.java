package com.iseplive.api.services;

import com.iseplive.api.dao.feed.FeedRepository;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.user.Student;
import org.springframework.data.domain.Page;
import com.iseplive.api.entity.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private PostService postService;

  public Feed getMain() {
    return feedRepository.findMain();
  }

  public Page<PostView> getMainPosts(int page) {
    Feed main = feedRepository.findMain();
    return postService.getFeedPosts(main, page);
  }

  public Page<PostView> getFeedPosts(String name, int page) {
    Feed feed = feedRepository.findFeedByName(name);
    return postService.getFeedPosts(feed, page);
  }

  public List<PostView> getFeedPostsWaiting(String name){
    Feed feed = feedRepository.findFeedByName(name);
    return postService.getFeedPostsWaiting(feed);
  }

  public List<PostView> getFeedPostsPinned(String name) {
    Feed feed = feedRepository.findFeedByName(name);
    return postService.getFeedPostsPinned(feed);
  }

  public List<PostView> getFeedDrafts(String name, Student author){
    Feed feed = feedRepository.findFeedByName(name);
    return postService.getFeedDrafts(feed, author);
  }

  public Feed createFeed(String name) {
    Feed feed = new Feed();
    feed.setName(name);

    feedRepository.save(feed);
    return feed;
  }

  public void deleteFeed(String name) {
    Feed feed = feedRepository.findFeedByName(name);

    if(feed != null)
      feedRepository.delete(feed);

  }
}
