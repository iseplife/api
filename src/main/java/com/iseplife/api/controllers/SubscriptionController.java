package com.iseplife.api.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.security.RolesAllowed;

import org.jose4j.lang.JoseException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lambdaexpression.annotation.EnableRequestBodyParam;
import com.github.lambdaexpression.annotation.RequestBodyParam;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.SubscribableType;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.services.ClubService;
import com.iseplife.api.services.EventService;
import com.iseplife.api.services.GroupService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@EnableRequestBodyParam
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;
  private final ClubService clubService;
  private final EventService eventService;
  private final GroupService groupService;
  
  @PutMapping("/{type}/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public void addSubscription(@PathVariable String type, @PathVariable Long id, @RequestBodyParam Boolean extensive) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    Subscription sub = subscriptionService.getSubscription(id);
    if(sub != null) {
      if(sub.isExtensive() != extensive) {
        sub.setExtensive(extensive);
        subscriptionService.updateSubscription(sub);
      }
      return;
    }
    
    Subscribable subbing;
    switch(type) {
      case SubscribableType.CLUB:
        subbing = clubService.getClub(id);
        break;
      case SubscribableType.EVENT:
        Event event = eventService.getEvent(id);
        if(SecurityService.hasReadAccessOn(event))
          throw new AccessDeniedException("No access to event");
        subbing = event;
        break;
      case SubscribableType.GROUP:
        Group group = groupService.getGroup(id);
        if(SecurityService.hasReadAccess(group))
          throw new AccessDeniedException("No access to group");
        subbing = group;
        break;
      default:
        throw new IllegalArgumentException("Cannot subscribe to a feed");
    }
    
    subscriptionService.subscribe(subbing);
  }
  @DeleteMapping("/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public void removeSubscription(@PathVariable Long id) {
    subscriptionService.unsubscribe(id);
  }
  @GetMapping("/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public Boolean isSubscribed(@PathVariable Long id) {
    return subscriptionService.isSubscribed(id);
  }
}
