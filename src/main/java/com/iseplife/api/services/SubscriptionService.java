package com.iseplife.api.services;

import com.iseplife.api.dao.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
  @Autowired
  SubscriptionRepository subscriptionRepository;

  @Autowired
  AuthService authService;

  public Boolean isSubscribedToFeed(Long id){
    return subscriptionRepository.existsSubscriptionByFeedIdAndListenerId(id, authService.getLoggedId());
  }
}
