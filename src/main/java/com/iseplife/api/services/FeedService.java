package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.SubscriptionRepository;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.Subscription;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.List;
import java.util.Optional;

@Service
public class FeedService {

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;


  @Autowired
  private PostService postService;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  @Autowired
  private StudentService studentService;

  public Feed getFeed(Long id) {
    Optional<Feed> feed = feedRepository.findById(id);
    if (feed.isEmpty())
      throw new IllegalArgumentException("could not find the feed with id: " + id);

    return feed.get();
  }

  public Iterable<Feed> getUserFeed(TokenPayload token) {
    Iterable<Feed> feeds = feedRepository.findAllById(token.getFeeds());

    return feeds;
  }

  @Cacheable("main-posts")
  public Page<PostView> getMainFeedPosts(int page) {
    Feed feed = getFeed(1L);
    return postService.getFeedPosts(feed, page);
  }

  @Cacheable("posts")
  public Page<PostView> getFeedPosts(Long id, int page) {
    Feed feed = getFeed(id);
    return postService.getFeedPosts(feed, page);
  }

  public List<PostView> getFeedPostsWaiting(Long id) {
    Feed feed = getFeed(id);
    return postService.getFeedPostsWaiting(feed);
  }

  public List<PostView> getFeedPostsPinned(Long id) {
    Feed feed = getFeed(id);
    return postService.getFeedPostsPinned(feed);
  }

  public List<PostView> getFeedDrafts(Long id, Student author) {
    Feed feed = getFeed(id);
    return postService.getFeedDrafts(feed, author);
  }

  public Boolean isSubscribedToFeed(Long id, Long studentID) {
    return subscriptionRepository.existsSubscriptionByFeedIdAndListenerId(id, studentID);
  }

  public Boolean isSubscribedToFeed(Feedable feedable) {
    return isSubscribedToFeed(feedable.getFeed().getId(), SecurityService.getLoggedId());
  }

  public Boolean toggleSubscription(Long id, Long studentID) {
    Feed feed = getFeed(id);
    if(!SecurityService.hasReadAccess(feed))
      throw new AuthException("You have not sufficient rights on this feed (id:" + id + ")");

    Subscription sub = subscriptionRepository.findByFeedIdAndListenerId(feed.getId(), studentID);
    if (sub != null) {
      subscriptionRepository.delete(sub);
      return false;
    } else {
      Student student = studentService.getStudent(studentID);
      sub = new Subscription();
      sub.setFeed(getFeed(id));
      sub.setListener(student);

      subscriptionRepository.save(sub);
      return true;
    }
  }
}
