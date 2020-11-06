package com.iseplife.api.services;

import com.iseplife.api.dao.feed.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
  @Autowired
  SubscriptionRepository subscriptionRepository;


  public Boolean isSubscribedToFeed(Long id){
    return subscriptionRepository.existsSubscriptionByFeedIdAndListenerId(id, SecurityService.getLoggedId());
  }
}
