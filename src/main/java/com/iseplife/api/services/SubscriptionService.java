package com.iseplife.api.services;

import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.dao.subscription.projection.SubscriptionProjection;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
  final private SubscriptionRepository subscriptionRepository;
  final private StudentService studentService;

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
  public void subscribe(Subscribable subable) {
    subscribe(subable, studentService.getStudent(SecurityService.getLoggedId()));
  }
  public void subscribe(Subscribable subable, Student student) {
    Subscription sub = new Subscription();
    sub.setListener(student);
    sub.setSubscribed(subable);
    subscriptionRepository.save(sub);
  }
  public void unsubscribe(Long id) {
    unsubscribe(id, SecurityService.getLoggedId());
  }
  public void unsubscribe(Long id, Long studentId) {
    subscriptionRepository.deleteBySubscribedIdAndListenerId(id, studentId);
  }
  
  public void updateSubscription(Subscription sub) {
    subscriptionRepository.save(sub);
  }
}
