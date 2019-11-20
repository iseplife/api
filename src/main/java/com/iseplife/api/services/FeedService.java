package com.iseplife.api.services;

import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.Subscription;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.feed.SubscriptionRepository;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

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

  private Feed getFeed(String name) {
    Feed feed = feedRepository.findByName(name);
    if (feed != null) {
      return feed;
    }
    throw new IllegalArgumentException("could not find the feed with name: " + name);
  }

  public Feed getMain() {
    return feedRepository.findMain();
  }

  public Feed createFeed(String name) {
    Feed feed = new Feed();
    feed.setName(name);

    feedRepository.save(feed);
    return feed;
  }

  public void deleteFeed(String name) {
    Feed feed = getFeed(name);
    feedRepository.delete(feed);
  }

  @Cacheable("main-posts")
  public Page<PostView> getMainPosts(int page) {
    Feed main = feedRepository.findMain();
    return postService.getFeedPosts(main, page);
  }

  @Cacheable("posts")
  public Page<PostView> getFeedPosts(String name, int page) {
    Feed feed = getFeed(name);
    return postService.getFeedPosts(feed, page);
  }

  public List<PostView> getFeedPostsWaiting(String name) {
    Feed feed = getFeed(name);
    return postService.getFeedPostsWaiting(feed);
  }

  public List<PostView> getFeedPostsPinned(String name) {
    Feed feed = getFeed(name);
    return postService.getFeedPostsPinned(feed);
  }

  public List<PostView> getFeedDrafts(String name, Student author) {
    Feed feed = getFeed(name);
    return postService.getFeedDrafts(feed, author);
  }

  public Boolean isSubscribed(String name, Long studentID){
    return subscriptionRepository.findByFeedNameAndListenerId(name, studentID) != null;
  }

  public void toggleSubscription(String name, Long studentID) {
    Subscription sub = subscriptionRepository.findByFeedNameAndListenerId(name, studentID);
    if (sub != null) {
      subscriptionRepository.delete(sub);
    }else {
      Student student = studentService.getStudent(studentID);
      sub = new Subscription();
      sub.setFeed(getFeed(name));
      sub.setListener(student);

      subscriptionRepository.save(sub);
    }
  }
}
