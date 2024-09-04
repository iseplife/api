package com.iseplife.api.services;

import java.util.List;

import com.iseplife.api.exceptions.http.HttpBadRequestException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.iseplife.api.constants.SubscribableType;
import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.websocket.services.WSPostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
  @Lazy final private StudentService studentService;
  @Lazy final private ClubService clubService;
  @Lazy final private EventService eventService;
  @Lazy final private GroupService groupService;
  @Lazy final private WSPostService postService;
  final private SubscriptionRepository subscriptionRepository;
  final private FirebaseMessengerService firebaseMessengerService;

  public Boolean isSubscribed(Subscribable sub){
    return isSubscribed(sub.getId(), SecurityService.getLoggedId());
  }
  public Boolean isSubscribed(Long id){
    return isSubscribed(id, SecurityService.getLoggedId());
  }
  public Boolean isSubscribed(Long id, Long studentId){
    return subscriptionRepository.existsSubscriptionBySubscribedIdAndListenerId(id, studentId);
  }
  public Subscription getSubscription(Long id) {
    return getSubscription(id, SecurityService.getLoggedId());
  }
  public Subscription getSubscription(Long id, Long studentId) {
    return subscriptionRepository.findBySubscribedIdAndListenerId(id, studentId);
  }
  public SubscriptionProjection getSubscriptionProjection(Subscribable subable) {
    return getSubscriptionProjection(subable, SecurityService.getLoggedId());
  }
  public SubscriptionProjection getSubscriptionProjection(Subscribable subable, Long studentId) {
    return subscriptionRepository.findProjectionBySubscribedAndListenerId(subable, studentId);
  }
  public SubscriptionProjection getSubscriptionProjection(Long id, Long studentId) {
    return subscriptionRepository.findProjectionBySubscribedIdAndListenerId(id, studentId);
  }
  public SubscriptionProjection getSubscriptionProjection(Long id) {
    return getSubscriptionProjection(id, SecurityService.getLoggedId());
  }
  public void subscribe(Subscribable subable, boolean extensive) {
    subscribe(subable, studentService.getStudent(SecurityService.getLoggedId()), extensive);
  }
  public List<Long> getSubscribedFeeds(Long studentId) {
    return subscriptionRepository.findFeedsByListenerId(studentId);
  }

  public void subscribeOrUpdateSub(String type, Long id, boolean extensive) {
    Subscription sub = getSubscription(id);
    if(sub != null) {
      if(sub.isExtensive() != extensive) {
        sub.setExtensive(extensive);
        updateSubscription(sub);
      }
      return;
    }

    Subscribable subbing = getSubscribable(type, id);
    subscribe(subbing, studentService.getStudent(SecurityService.getLoggedId()), extensive);
  }

  public void subscribe(Subscribable subable, Student student, boolean extensive) {
    if(subscriptionRepository.existsBySubscribedAndListener_Id(subable, student.getId()))
      throw new HttpBadRequestException("already_subscribed");

    Subscription sub = new Subscription();
    sub.setListener(student);
    sub.setSubscribed(subable);
    sub.setSubscribedFeed(subable.getFeed());
    sub.setExtensive(extensive);

    updateSubscription(sub);
    
    postService.addStudentToFeed(student.getId(), subable.getFeed().getId());
  }
  public void unsubscribe(Long id) {
    unsubscribe(id, SecurityService.getLoggedId());
  }
  public void unsubscribe(Long id, Long studentId) {
    postService.removeStudentFromFeed(studentId, subscriptionRepository.findFeedBySubscribedIdAndListenerId(id, studentId));
    subscriptionRepository.deleteBySubscribedIdAndListenerId(id, studentId);

    firebaseMessengerService.unsubFromTopic(studentService.getStudent(studentId), id);
  }

  public void updateSubscription(Subscription sub) {
    if(sub.isExtensive()) {
      firebaseMessengerService.subToTopic(sub.getListener(), sub.getSubscribedFeed());
    } else {
      firebaseMessengerService.unsubFromTopic(sub.getListener(), sub.getSubscribedFeed());
    }
    subscriptionRepository.save(sub);
  }

  public Subscribable getSubscribable(String type, Long id) {
    switch(type) {
      case SubscribableType.CLUB:
        return clubService.getClub(id);
      case SubscribableType.STUDENT:
        return studentService.getStudent(id);
      case SubscribableType.EVENT:
        Event event = eventService.getEvent(id);
        if(!SecurityService.hasReadAccessOn(event))
          throw new AccessDeniedException("No access to event");
        return event;
      case SubscribableType.GROUP:
        Group group = groupService.getGroup(id);
        if(!SecurityService.hasReadAccess(group))
          throw new AccessDeniedException("No access to group");
        return group;
      default:
        throw new IllegalArgumentException("Cannot subscribe to a feed");
    }
  }
}
