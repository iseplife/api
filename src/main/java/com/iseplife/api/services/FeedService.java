package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.FeedProjection;
import com.iseplife.api.dao.feed.SubscriptionRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.feed.Feedable;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
  @Lazy final private PostService postService;
  @Lazy final private StudentService studentService;
  final private FeedRepository feedRepository;
  final private SubscriptionRepository subscriptionRepository;

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

  public Boolean isSubscribedToFeed(Long id, Long studentID) {
    return subscriptionRepository.existsSubscriptionBySubscribedIdAndListenerId(id, studentID);
  }

  public Boolean isSubscribedToFeed(Feedable feedable) {
    return isSubscribedToFeed(feedable.getFeed().getId(), SecurityService.getLoggedId());
  }

  public Boolean toggleSubscription(Long id, Long studentID) {
    Feed feed = getFeed(id);
    if(!SecurityService.hasReadAccess(feed))
      throw new HttpForbiddenException("insufficient_rights");

    Subscription sub = subscriptionRepository.findByFeedIdAndListenerId(feed.getId(), studentID);
    if (sub != null) {
      subscriptionRepository.delete(sub);
      return false;
    } else {
      Student student = studentService.getStudent(studentID);
      sub = new Subscription();
      sub.setSubscribed(getFeed(id));
      sub.setListener(student);

      subscriptionRepository.save(sub);
      return true;
    }
  }
}
