package com.iseplife.api.services;

import com.iseplife.api.dao.feed.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
  final private SubscriptionRepository subscriptionRepository;

  public Boolean isSubscribedToFeed(Long id){
    return subscriptionRepository.existsSubscriptionBySubscribedIdAndListenerId(id, SecurityService.getLoggedId());
  }
}
