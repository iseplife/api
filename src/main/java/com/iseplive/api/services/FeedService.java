package com.iseplive.api.services;

import com.iseplive.api.dao.feed.FeedRepository;
import com.iseplive.api.dao.feed.SubscriptionRepository;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.Subscription;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.IllegalArgumentException;
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
  private SubscriptionRepository subscriptionRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private StudentService studentService;

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

  public void subscribeFeed(String name, Long studentId){
    Feed feed = feedRepository.findFeedByName(name);

    if(feed != null){
      Student student = studentService.getStudent(studentId);
      Subscription sub = new Subscription();
      sub.setFeed(feed);
      sub.setListener(student);

      subscriptionRepository.save(sub);
    }
    throw new IllegalArgumentException("could not find the feed with name: " + name);
  }
}
